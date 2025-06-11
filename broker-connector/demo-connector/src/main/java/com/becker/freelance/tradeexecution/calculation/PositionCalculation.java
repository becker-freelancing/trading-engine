package com.becker.freelance.tradeexecution.calculation;

import com.becker.freelance.commons.calculation.ProfitLossCalculation;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PositionCalculation {


    public record PositionCalculationResult(List<Position> positions, List<Trade> trades) {
    }

    private final TradingCalculator tradingCalculator;
    private final TradingFeeCalculator tradingFeeCalculator;

    public PositionCalculation(TradingCalculator tradingCalculator, TradingFeeCalculator tradingFeeCalculator) {
        this.tradingCalculator = tradingCalculator;
        this.tradingFeeCalculator = tradingFeeCalculator;
    }


    public PositionCalculationResult openPosition(TimeSeriesEntry currentPrice, List<Position> positions, Position position, Wallet wallet) {
        if (!position.getOpenOrder().isExecuted()) {
            throw new IllegalStateException("Open Order of Position must be executed first");
        }
        if (positions.isEmpty()) {
            return openSinglePosition(position, wallet);
        }


        return switch (position.getDirection()) {
            case BUY -> openBuyPosition(currentPrice, positions, position, wallet);
            case SELL -> openSellPosition(currentPrice, positions, position, wallet);
        };
    }

    private PositionCalculationResult openSellPosition(TimeSeriesEntry currentPrice, List<Position> positions, Position position, Wallet wallet) {
        Direction existingDirection = positions.get(0).getDirection();

        return switch (existingDirection) {
            case BUY -> addBuyPositionToSellPositions(currentPrice, positions, position, wallet);
            case SELL -> addSameDirectionPosition(positions, position, wallet);
        };
    }


    private PositionCalculationResult openBuyPosition(TimeSeriesEntry currentPrice, List<Position> positions, Position position, Wallet wallet) {
        Direction existingDirection = positions.get(0).getDirection();

        return switch (existingDirection) {
            case BUY -> addSameDirectionPosition(positions, position, wallet);
            case SELL -> addBuyPositionToSellPositions(currentPrice, positions, position, wallet);
        };
    }

    private PositionCalculationResult addBuyPositionToSellPositions(TimeSeriesEntry currentPrice, List<Position> positions, Position position, Wallet wallet) {
        List<Position> positionsToClose = new ArrayList<>();
        Decimal sizeToOpen = position.getSize();
        boolean completelyEliminated = false;
        boolean sizeToOpenEliminated = false;
        Position positionToPartlyClose = null;
        for (Position openPosition : new ArrayList<>(positions)) {
            Decimal sizeAfterAdaption = sizeToOpen.subtract(openPosition.getSize());
            if (sizeAfterAdaption.isEqualToZero()) {
                positionsToClose.add(openPosition);
                completelyEliminated = true;
                sizeToOpen = sizeAfterAdaption;
                break;
            } else if (sizeAfterAdaption.isGreaterThanZero()) {
                positionsToClose.add(openPosition);
                sizeToOpen = sizeAfterAdaption;
            } else if (sizeAfterAdaption.isLessThanZero()) {
                sizeToOpenEliminated = true;
                positionToPartlyClose = openPosition;
                break;
            }
        }

        if (!completelyEliminated && !sizeToOpenEliminated && sizeToOpen.isGreaterThanZero()) {
            //Close all existing and open new position with remaining size
            return closeAllExistingPositionsAndOpenNewPositionWithRemainingSize(currentPrice, positions, position, wallet, sizeToOpen);
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

    private PositionCalculationResult closeAllExistingPositionsAndOpenNewPositionWithRemainingSize(TimeSeriesEntry currentPrice, List<Position> positions, Position position, Wallet wallet, Decimal sizeToOpen) {
        PositionCalculationResult positionCalculationResult = closePositions(currentPrice, positions, wallet);
        position.setSize(sizeToOpen);
        List<Position> openedPositions = new ArrayList<>();
        if (wallet.canOpen(position.getMargin())) {
            wallet.addMargin(position.getMargin());
            openedPositions.add(position);
        }
        return new PositionCalculationResult(openedPositions, positionCalculationResult.trades());
    }

    private Position clonePositionWithDifferentSize(Position position, Decimal size) {
        return position.cloneWithSize(size);
    }

    private PositionCalculationResult closePositions(TimeSeriesEntry currentPrice, List<Position> positions, Wallet wallet) {
        List<Trade> closedTrades = new ArrayList<>();
        for (Position position : positions) {
            ProfitLossCalculation profitConversionRate = tradingCalculator.getProfitInEuroWithoutFees(position, currentPrice, currentPrice.time());
            Trade trade = toTrade(profitConversionRate.conversionRate(), profitConversionRate.profit(), profitConversionRate.closePrice(), currentPrice.pair(), position, currentPrice);
            closedTrades.add(trade);
            wallet.adjustAmount(trade.getProfitInEuroWithFees());
            wallet.removeMargin(position.getMargin());
        }

        return new PositionCalculationResult(new ArrayList<>(), closedTrades);
    }

    private Trade toTrade(Decimal conversionRate, Decimal profit, Decimal closePrice, Pair pair, Position position, TimeSeriesEntry currentPrice) {
        Decimal exitTradingFee = exitTradingFee(position, closePrice);
        Decimal openFee = position.getOpenFee();
        Decimal profitWithFees = profit.subtract(openFee).subtract(exitTradingFee);
        return new Trade(position.getId(), position.getOpenTime(), currentPrice.time(), pair, profitWithFees,
                position.getOpenPrice(), closePrice, openFee, exitTradingFee, position.getSize(), position.getDirection(),
                conversionRate, position.getPositionType(), position.getOpenMarketRegime());
    }

    private Decimal exitTradingFee(Position position, Decimal closePrice) {
        if (position.isAnyCloseTaker()) {
            return tradingFeeCalculator.calculateTakerTradingFeeInCounterCurrency(closePrice, position.getSize());
        }

        return tradingFeeCalculator.calculateMakerTradingFeeInCounterCurrency(closePrice, position.getSize());
    }


    private PositionCalculationResult addSameDirectionPosition(List<Position> positions, Position position, Wallet wallet) {
        if (wallet.canOpen(position.getMargin())) {
            wallet.addMargin(position.getMargin());
            positions.add(position);
        }
        List<Trade> trades = new ArrayList<>();
        return new PositionCalculationResult(positions, trades);
    }

    private PositionCalculationResult openSinglePosition(Position position, Wallet wallet) {
        List<Position> positions = new ArrayList<>();
        List<Trade> trades = new ArrayList<>();

        if (wallet.canOpen(position.getMargin())) {
            wallet.addMargin(position.getMargin());
            positions.add(position);
        }

        return new PositionCalculationResult(positions, trades);
    }

    private PositionCalculationResult closeSlReachedPositions(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        List<Trade> stopReachedTrades = openPositions.stream()
                .filter(position -> position.getStopOrder().canBeExecuted(currentPrice))
                .filter(position -> isStopReached(position, currentPrice))
                .map(Position::clone)
                .peek(position -> position.getStopOrder().executeIfPossible(currentPrice))
                .filter(position -> position.getStopOrder().isExecuted())
                .peek(position -> wallet.removeMargin(position.getMargin()))
                .map(position -> toTrade(position, position.getExecutedStopPrice(), currentPrice))
                .peek(trade -> wallet.adjustAmount(trade.getProfitInEuroWithFees()))
                .toList();

        Set<String> closedPositionsIds = stopReachedTrades.stream().map(Trade::getRelatedPositionId).collect(Collectors.toSet());
        List<Position> remainingPositions = openPositions.stream().filter(position -> !closedPositionsIds.contains(position.getId())).toList();
        return new PositionCalculationResult(remainingPositions, stopReachedTrades);
    }

    private PositionCalculationResult closeTpReachedPositions(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        List<Trade> stopReachedTrades = openPositions.stream()
                .filter(position -> position.getLimitOrder().canBeExecuted(currentPrice))
                .filter(position -> isLimitReached(position, currentPrice))
                .map(Position::clone)
                .peek(position -> position.getLimitOrder().executeIfPossible(currentPrice))
                .filter(position -> position.getLimitOrder().isExecuted())
                .peek(position -> wallet.removeMargin(position.getMargin()))
                .map(position -> toTrade(position, position.getExecutedLimitPrice(), currentPrice))
                .peek(trade -> wallet.adjustAmount(trade.getProfitInEuroWithFees()))
                .toList();

        Set<String> closedPositionsIds = stopReachedTrades.stream().map(Trade::getRelatedPositionId).collect(Collectors.toSet());
        List<Position> remainingPositions = openPositions.stream().filter(position -> !closedPositionsIds.contains(position.getId())).toList();
        return new PositionCalculationResult(remainingPositions, stopReachedTrades);
    }


    public PositionCalculationResult closePositionIfSlOrTpReached(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        PositionCalculationResult slClosedResult = closeSlReachedPositions(currentPrice, openPositions, wallet);
        PositionCalculationResult tpClosedResult = closeTpReachedPositions(currentPrice, slClosedResult.positions(), wallet);

        List<Trade> allClosedTrades = new ArrayList<>(slClosedResult.trades());
        allClosedTrades.addAll(tpClosedResult.trades());
        return new PositionCalculationResult(new ArrayList<>(tpClosedResult.positions()), allClosedTrades);
    }

    private Trade toTrade(Position position, Decimal executedPrice, TimeSeriesEntry currentPrice) {
        ProfitLossCalculation profitInEuro = tradingCalculator.getProfitInEuroWithoutFees(position, executedPrice, currentPrice.time());
        return toTrade(profitInEuro.conversionRate(), profitInEuro.profit(), profitInEuro.closePrice(), position.getPair(), position, currentPrice);
    }


    private boolean isLimitReached(Position position, TimeSeriesEntry currentPrice) {
        Decimal currentTpPrice = currentLimitPrice(position, currentPrice);
        Decimal estimatedLimitLevel = position.getEstimatedLimitLevel(currentPrice);
        return switch (position.getDirection()) {
            case BUY -> currentTpPrice.isGreaterThanOrEqualTo(estimatedLimitLevel);
            case SELL -> currentTpPrice.isLessThanOrEqualTo(estimatedLimitLevel);
        };
    }


    private Decimal currentLimitPrice(Position position, TimeSeriesEntry currentPrice) {
        return switch (position.getDirection()) {
            case BUY -> currentPrice.closeBid().max(currentPrice.highBid());
            case SELL -> currentPrice.closeAsk().min(currentPrice.lowAsk());
        };
    }

    private boolean isStopReached(Position position, TimeSeriesEntry currentPrice) {
        Decimal currentSlPrice = currentStopPrice(position, currentPrice);
        Decimal estimatedStopLevel = position.getEstimatedStopLevel(currentPrice);
        return switch (position.getDirection()) {
            case BUY -> currentSlPrice.isLessThanOrEqualTo(estimatedStopLevel);
            case SELL -> currentSlPrice.isGreaterThanOrEqualTo(estimatedStopLevel);
        };
    }


    private Decimal currentStopPrice(Position position, TimeSeriesEntry currentPrice) {
        return switch (position.getDirection()) {
            case BUY -> currentPrice.closeBid().min(currentPrice.lowBid());
            case SELL -> currentPrice.closeAsk().max(currentPrice.highAsk());
        };
    }


    public PositionCalculationResult closeAllBuyPositions(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        Predicate<Position> criteria = position -> Direction.BUY.equals(position.getDirection());
        return closePositionsMatchingCriteria(openPositions, currentPrice, wallet, criteria);
    }


    public PositionCalculationResult closeAllSellPositions(TimeSeriesEntry currentPrice, List<Position> openPositions, Wallet wallet) {
        Predicate<Position> criteria = position -> Direction.SELL.equals(position.getDirection());
        return closePositionsMatchingCriteria(openPositions, currentPrice, wallet, criteria);
    }

    private PositionCalculationResult closePositionsMatchingCriteria(List<Position> positions, TimeSeriesEntry currentPrice, Wallet wallet, Predicate<Position> criteria) {
        List<Position> positionsToClose = positions.stream().filter(criteria).toList();
        List<Position> remainingPositions = positions.stream().filter(position -> !positionsToClose.contains(position)).toList();
        PositionCalculationResult closePositionsResult = closePositions(currentPrice, positionsToClose, wallet);

        return new PositionCalculationResult(new ArrayList<>(remainingPositions), closePositionsResult.trades());
    }
}
