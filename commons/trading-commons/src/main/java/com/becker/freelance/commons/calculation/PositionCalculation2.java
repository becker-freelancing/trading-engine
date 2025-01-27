package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.HardLimitPosition;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.position.TrailingStopPosition;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionCalculation2 {


    public static record PositionCalculationResult(List<Position> positions, List<Trade> trades){}

    private final TradingCalculator tradingCalculator;
    private final MarginCalculator marginCalculator;

    public PositionCalculation2(TradingCalculator tradingCalculator, MarginCalculator marginCalculator) {
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
            case SELL -> addBuyPositionToSellPositions(currentPrice, positions, entrySignal, wallet);//addSellPositionToBuyPositions(currentPrice, positions, entrySignal, wallet);
        };
    }

    private PositionCalculationResult addBuyPositionToSellPositions(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
        List<Position> positionsToClose = new ArrayList<>();
        double sizeToOpen = entrySignal.getSize();
        boolean completelyEliminated = false;
        boolean sizeToOpenEliminated = false;
        Position positionToPartlyClose = null;
        for (Position position : new ArrayList<>(positions)) {
            double sizeAfterAdaption = sizeToOpen - position.getSize();
            if (sizeAfterAdaption == 0){
                positionsToClose.add(position);
                completelyEliminated = true;
                sizeToOpen = sizeAfterAdaption;
                break;
            } else if (sizeAfterAdaption > 0) {
                positionsToClose.add(position);
                sizeToOpen = sizeAfterAdaption;
            } else if (sizeAfterAdaption < 0) {
                sizeToOpenEliminated = true;
                positionToPartlyClose = position;
                break;
            }
        }

        if (!completelyEliminated && !sizeToOpenEliminated && sizeToOpen > 0){
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

    private PositionCalculationResult closeAllPositionsAndCloseOnePartial(TimeSeriesEntry currentPrice, List<Position> positions, Wallet wallet, Position positionToPartlyClose, double sizeToOpen, List<Position> positionsToClose) {
        double partToClose = positionToPartlyClose.getSize() - sizeToOpen;
        double partToRemainOpen = positionToPartlyClose.getSize() - partToClose;
        Position partialClosePosition = clonePositionWithDifferentSize(positionToPartlyClose, partToClose);
        Position partialOpenPosition = clonePositionWithDifferentSize(positionToPartlyClose, partToRemainOpen);
        positionsToClose.add(partialClosePosition);
        PositionCalculationResult closedPositionsResult = closePositions(currentPrice, positionsToClose, wallet);

        int indexOfPartialClosePosition = positions.indexOf(positionToPartlyClose);
        List<Position> positionsToRemainOpen = positions.subList(indexOfPartialClosePosition + 1, positions.size());
        positionsToRemainOpen.add(0, partialOpenPosition);
        return new PositionCalculationResult(positionsToRemainOpen, closedPositionsResult.trades());
    }

    private PositionCalculationResult closeAllExistingPositionsAndOpenNewPositionWithRemainingSize(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet, double sizeToOpen) {
        PositionCalculationResult positionCalculationResult = closePositions(currentPrice, positions, wallet);
        entrySignal.setSize(sizeToOpen);
        Optional<Position> newPosition = toPosition(currentPrice, entrySignal, wallet);
        List<Position> openedPositions = new ArrayList<>();
        newPosition.ifPresent(openedPositions::add);
        return new PositionCalculationResult(openedPositions, positionCalculationResult.trades());
    }

    private Position clonePositionWithDifferentSize(Position position, double size) {
        TimeSeriesEntry openPrice = position.getOpenPrice();
        double margin = marginCalculator.calcMargin(size, openPrice);
        return switch (position.getPositionType()){
            case HARD_LIMIT -> new HardLimitPosition(size, position.getDirection(), openPrice, position.getPair(), position.getStopInPoints(), position.getLimitInPoints(), margin);
            case TRAILING -> new TrailingStopPosition(size, position.getDirection(), openPrice, position.getPair(), position.getStopInPoints(), position.getLimitInPoints(), margin, ((TrailingStopPosition) position).getTrailingStepSize());
        };
    }

    private PositionCalculationResult closePositions(TimeSeriesEntry currentPrice, List<Position> positions, Wallet wallet) {
        List<Trade> closedTrades = new ArrayList<>();
        for (Position position : positions) {
            TradingCalculator.ProfitLossResult profitConversionRate = position.currentProfit(currentPrice, tradingCalculator);
            closedTrades.add(toTrade(profitConversionRate.conversionRate(), profitConversionRate.profit(), currentPrice.pair(), position, currentPrice));
            wallet.adjustAmount(profitConversionRate.profit());
            wallet.removeMargin(position.getMargin());
        }

        return new PositionCalculationResult(new ArrayList<>(), closedTrades);
    }

    private Trade toTrade(double conversionRate, double profit, Pair pair, Position position, TimeSeriesEntry currentPrice) {
        return new Trade(position.getOpenTime(), currentPrice.time(), pair, profit,
                position.getOpenPriceAsNumber(), position.currentPrice(currentPrice), position.getSize(),
                position.getDirection(), conversionRate, position.getPositionType());
    }


//    private PositionCalculationResult addSellPositionToBuyPositions(TimeSeriesEntry currentPrice, List<Position> positions, EntrySignal entrySignal, Wallet wallet) {
//
//    }

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
        double margin = marginCalculator.calcMargin(entrySignal.getSize(), currentPrice);
        if (!wallet.canOpen(margin)){
            return Optional.empty();
        }
        wallet.addMargin(margin);
        return switch (entrySignal.getPositionType()) {
            case HARD_LIMIT -> Optional.of(new HardLimitPosition(entrySignal.getSize(), entrySignal.getDirection(), currentPrice, currentPrice.pair(), entrySignal.getStopInPoints(), entrySignal.getLimitInPoints(), margin));
            case TRAILING -> Optional.of(new TrailingStopPosition(entrySignal.getSize(), entrySignal.getDirection(), currentPrice, currentPrice.pair(), entrySignal.getStopInPoints(), entrySignal.getLimitInPoints(), entrySignal.getTrailingStepSize(), margin));
        };
    }
}
