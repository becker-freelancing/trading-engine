package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.HardLimitPosition;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.position.TrailingStopPosition;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.EuroDistanceEntrySignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class PositionCalculation {


    public record PositionCalculationResult(List<Position> positions, List<Trade> trades) {
    }

    private final TradingCalculator tradingCalculator;
    private final MarginCalculator marginCalculator;

    public PositionCalculation(TradingCalculator tradingCalculator, MarginCalculator marginCalculator) {
        this.tradingCalculator = tradingCalculator;
        this.marginCalculator = marginCalculator;
    }


    public PositionCalculationResult openPosition(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
        if (positions.isEmpty()){
            return openSinglePosition(currentPrice, entrySignal, wallet);
        }


        return switch (entrySignal.getDirection()){
            case BUY -> openBuyPosition(currentPrice, positions, entrySignal, wallet);
            case SELL -> openSellPosition(currentPrice, positions, entrySignal, wallet);
        };
    }

    private PositionCalculationResult openSellPosition(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
        Direction existingDirection = positions.get(0).getDirection();

        return switch (existingDirection){
            case BUY -> addBuyPositionToSellPositions(currentPrice, positions, entrySignal, wallet);
            case SELL -> addSameDirectionPosition(currentPrice, positions, entrySignal, wallet);
        };
    }


    private PositionCalculationResult openBuyPosition(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
        Direction existingDirection = positions.get(0).getDirection();

        return switch (existingDirection){
            case BUY -> addSameDirectionPosition(currentPrice, positions, entrySignal, wallet);
            case SELL -> addBuyPositionToSellPositions(currentPrice, positions, entrySignal, wallet);
        };
    }

    private PositionCalculationResult addBuyPositionToSellPositions(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
        List<Position> positionsToClose = new ArrayList<>();
        Decimal sizeToOpen = entrySignal.getSize();
        boolean completelyEliminated = false;
        boolean sizeToOpenEliminated = false;
        Position positionToPartlyClose = null;
        for (Position position : new ArrayList<>(positions)) {
            Decimal sizeAfterAdaption = sizeToOpen.subtract(position.getSize());
            if (sizeAfterAdaption.isEqualToZero()){
                positionsToClose.add(position);
                completelyEliminated = true;
                sizeToOpen = sizeAfterAdaption;
                break;
            } else if (sizeAfterAdaption.isGreaterThanZero()) {
                positionsToClose.add(position);
                sizeToOpen = sizeAfterAdaption;
            } else if (sizeAfterAdaption.isLessThanZero()) {
                sizeToOpenEliminated = true;
                positionToPartlyClose = position;
                break;
            }
        }

        if (!completelyEliminated && !sizeToOpenEliminated && sizeToOpen.isGreaterThanZero()){
            //Close all existing and open new position with remaining size
            return closeAllExistingPositionsAndOpenNewPositionWithRemainingSize(currentPrice, positions, entrySignal, wallet, sizeToOpen);
        } else if (completelyEliminated) {
            //Close all positions and do not open a new position
            return closePositions(currentPrice, positions, wallet);
        } else if (sizeToOpenEliminated) {
            // Close all Positions to close and partially close one position, but leave open
            return closeAllPositionsAndCloseOnePartial(currentPrice, positions, wallet, positionToPartlyClose, sizeToOpen, positionsToClose);
        }

        throw new IllegalStateException("Could not open positions");
    }

    private PositionCalculationResult closeAllPositionsAndCloseOnePartial(TimeSeriesEntry currentPrice, List<Position> positions, Wallet wallet, Position positionToPartlyClose, Decimal sizeToOpen, List<Position> positionsToClose) {
        Decimal partToClose = positionToPartlyClose.getSize().subtract(sizeToOpen);
        Decimal partToRemainOpen = positionToPartlyClose.getSize().subtract(partToClose);
        Position partialClosePosition = clonePositionWithDifferentSize(positionToPartlyClose, partToClose);
        Position partialOpenPosition = clonePositionWithDifferentSize(positionToPartlyClose, partToRemainOpen);
        positionsToClose.add(partialClosePosition);
        PositionCalculationResult closedPositionsResult = closePositions(currentPrice, positionsToClose, wallet);

        int indexOfPartialClosePosition = positions.indexOf(positionToPartlyClose);
        List<Position> positionsToRemainOpen = positions.subList(indexOfPartialClosePosition + 1, positions.size());
        positionsToRemainOpen.add(0, partialOpenPosition);
        return new PositionCalculationResult(positionsToRemainOpen, closedPositionsResult.trades());
    }

    private PositionCalculationResult closeAllExistingPositionsAndOpenNewPositionWithRemainingSize(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet, Decimal sizeToOpen) {
        PositionCalculationResult positionCalculationResult = closePositions(currentPrice, positions, wallet);
        entrySignal.setSize(sizeToOpen);
        Optional<Position> newPosition = toPosition(currentPrice, entrySignal, wallet);
        List<Position> openedPositions = new ArrayList<>();
        newPosition.ifPresent(openedPositions::add);
        return new PositionCalculationResult(openedPositions, positionCalculationResult.trades());
    }

    private Position clonePositionWithDifferentSize(Position position, Decimal size) {
        TimeSeriesEntry openPrice = position.getOpenPrice();
        Decimal margin = marginCalculator.calcMargin(size, openPrice);
        return switch (position.getPositionType()){
            case HARD_LIMIT -> HardLimitPosition.fromLevels(tradingCalculator, size, position.getDirection(), openPrice, position.getPair(), position.getStopLevel(), position.getLimitLevel(), margin);
            case TRAILING -> TrailingStopPosition.fromLevels(tradingCalculator, size, position.getDirection(), openPrice, position.getPair(), position.getStopLevel(), position.getLimitLevel(), margin, ((TrailingStopPosition) position).getTrailingStepSizeInEuro());
        };
    }

    private PositionCalculationResult closePositions(TimeSeriesEntry currentPrice, List<Position> positions, Wallet wallet) {
        List<Trade> closedTrades = new ArrayList<>();
        for (Position position : positions) {
            TradingCalculator.ProfitLossResult profitConversionRate = position.closeProfit(currentPrice);
            closedTrades.add(toTrade(profitConversionRate.conversionRate(), profitConversionRate.profit(), currentPrice.pair(), position, currentPrice));
            wallet.adjustAmount(profitConversionRate.profit());
            wallet.removeMargin(position.getMargin());
        }

        return new PositionCalculationResult(new ArrayList<>(), closedTrades);
    }

    private Trade toTrade(Decimal conversionRate, Decimal profit, Pair pair, Position position, TimeSeriesEntry currentPrice) {
        if (position instanceof TrailingStopPosition trailingStopPosition) {
            return new Trade(position.getOpenTime(), currentPrice.time(), pair, profit,
                    position.getOpenPriceAsNumber(), position.currentPrice(currentPrice), position.getSize(),
                    position.getDirection(), conversionRate, position.getPositionType(), trailingStopPosition.getTrailingStepSizeInEuro());
        }
        return new Trade(position.getOpenTime(), currentPrice.time(), pair, profit,
                position.getOpenPriceAsNumber(), position.currentPrice(currentPrice), position.getSize(),
                position.getDirection(), conversionRate, position.getPositionType());
    }


    private PositionCalculationResult addSameDirectionPosition(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
        Optional<Position> position = toPosition(currentPrice, entrySignal, wallet);
        List<Trade> trades = new ArrayList<>();
        position.ifPresent(positions::add);
        return new PositionCalculationResult(positions, trades);
    }

    private PositionCalculationResult openSinglePosition(TimeSeriesEntry currentPrice, EntrySignal entrySignal, Wallet wallet) {
        Optional<Position> position = toPosition(currentPrice, entrySignal, wallet);
        List<Position> positions = new ArrayList<>();
        List<Trade> trades = new ArrayList<>();
        position.ifPresent(positions::add);
        return new PositionCalculationResult(positions, trades);
    }

    private Optional<Position> toPosition(TimeSeriesEntry currentPrice, EntrySignal entrySignal, Wallet wallet) {
        Decimal margin = marginCalculator.calcMargin(entrySignal.getSize(), currentPrice);
        if (!wallet.canOpen(margin)){
            return Optional.empty();
        }
        wallet.addMargin(margin);
        if (entrySignal instanceof EuroDistanceEntrySignal euroDistanceEntrySignal){
            return switch (entrySignal.getPositionType()) {
                case HARD_LIMIT -> Optional.of(HardLimitPosition.fromDistancesInEuros(tradingCalculator, entrySignal.getSize(), entrySignal.getDirection(), currentPrice, currentPrice.pair(), euroDistanceEntrySignal.getStopInEuros(), euroDistanceEntrySignal.getLimitInEuros(), margin));
                case TRAILING -> Optional.of(TrailingStopPosition.fromDistancesInEuro(tradingCalculator, entrySignal.getSize(), entrySignal.getDirection(), currentPrice, currentPrice.pair(), euroDistanceEntrySignal.getStopInEuros(), euroDistanceEntrySignal.getLimitInEuros(), entrySignal.getTrailingStepSize(), margin));
            };
        } else if (entrySignal instanceof LevelEntrySignal levelEntrySignal){
            return switch (entrySignal.getPositionType()) {
                case HARD_LIMIT -> Optional.of(HardLimitPosition.fromLevels(tradingCalculator, entrySignal.getSize(), entrySignal.getDirection(), currentPrice, currentPrice.pair(), levelEntrySignal.getStopLevel(), levelEntrySignal.getLimitLevel(), margin));
                case TRAILING -> Optional.of(TrailingStopPosition.fromLevels(tradingCalculator, entrySignal.getSize(), entrySignal.getDirection(), currentPrice, currentPrice.pair(), levelEntrySignal.getStopLevel(), levelEntrySignal.getLimitLevel(), entrySignal.getTrailingStepSize(), margin));
            };
        }

        throw new IllegalStateException("Could not map EntrySignal of type " + entrySignal.getClass() + " to position");
    }


    public PositionCalculationResult closePositionIfSlOrTpReached(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        Predicate<Position> criteria = position -> position.isSlReached(currentPrice) || position.isTpReached(currentPrice);
        return closePositionsMatchingCriteria(openPositions, currentPrice, wallet, criteria);
    }


    public PositionCalculationResult closeAllBuyPositions(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        Predicate<Position> criteria = position -> Direction.BUY.equals(position.getDirection());
        return closePositionsMatchingCriteria(openPositions, currentPrice, wallet, criteria);
    }


    public PositionCalculationResult closeAllSellPositions(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        Predicate<Position> criteria = position -> Direction.SELL.equals(position.getDirection());
        return closePositionsMatchingCriteria(openPositions, currentPrice, wallet, criteria);
    }

    private PositionCalculationResult closePositionsMatchingCriteria(List<Position> positions, TimeSeriesEntry currentPrice, Wallet wallet, Predicate<Position> criteria){
        List<Position> positionsToClose = positions.stream().filter(criteria).toList();
        List<Position> remainingPositions = positions.stream().filter(position -> !positionsToClose.contains(position)).toList();
        PositionCalculationResult closePositionsResult = closePositions(currentPrice, positionsToClose, wallet);

        return new PositionCalculationResult(new ArrayList<>(remainingPositions), closePositionsResult.trades());
    }
}
