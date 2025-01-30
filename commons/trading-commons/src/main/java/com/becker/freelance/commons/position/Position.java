package com.becker.freelance.commons.position;


import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Position {

    protected static Decimal getOpenPriceAsNumber(Direction direction, TimeSeriesEntry openPrice) {
        return switch (direction) {
            case BUY -> openPrice.closeAsk();
            case SELL -> openPrice.closeBid();
        };
    }

    protected static Decimal getClosePriceAsNumber(Direction direction, TimeSeriesEntry openPrice) {
        return switch (direction) {
            case SELL -> openPrice.closeAsk();
            case BUY -> openPrice.closeBid();
        };
    }

    protected static Decimal getStopLevelFromDistanceInEuro(TradingCalculator tradingCalculator, Direction direction, TimeSeriesEntry openPrice, Decimal distance, Decimal size, Pair pair) {
        Decimal openPriceAsNumber = getOpenPriceAsNumber(direction, openPrice);
        Decimal profitPerPoint = profitPerPoint(size, pair);
        Decimal absDistanceInPoints = tradingCalculator.calcDistanceInEurosFromDistanceInPointsAbsolute(distance, size, openPrice.time(), profitPerPoint);
        return switch (direction) {
            case BUY -> openPriceAsNumber.subtract(absDistanceInPoints).max(Decimal.ZERO);
            case SELL -> openPriceAsNumber.add(absDistanceInPoints);
        };
    }

    protected static Decimal getLimitLevelFromDistanceInEuro(TradingCalculator tradingCalculator, Direction direction, TimeSeriesEntry openPrice, Decimal distance, Decimal size, Pair pair) {
        Decimal openPriceAsNumber = getOpenPriceAsNumber(direction, openPrice);
        Decimal profitPerPoint = profitPerPoint(size, pair);
        Decimal absDistanceInPoints = tradingCalculator.calcDistanceInEurosFromDistanceInPointsAbsolute(distance, size, openPrice.time(), profitPerPoint);
        return switch (direction) {
            case BUY -> openPriceAsNumber.add(absDistanceInPoints);
            case SELL -> openPriceAsNumber.subtract(distance).max(Decimal.ZERO);
        };
    }

    protected static Decimal profitPerPoint(Decimal size, Pair pair) {
        return size.multiply(pair.profitPerPointForOneContract()).multiply(pair.sizeMultiplication());
    }

    protected TradingCalculator tradingCalculator;
    protected Decimal size;
    protected Direction direction;
    protected Pair pair;
    protected TimeSeriesEntry openPrice;
    protected LocalDateTime openTime;
    protected Decimal stopLevel;
    protected Decimal limitLevel;
    protected PositionType positionType;
    protected Decimal margin;

    Position(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
             Decimal stopLevel, Decimal limitLevel, PositionType positionType, Decimal margin) {
        checkLevels(direction, stopLevel, limitLevel);
        this.tradingCalculator = tradingCalculator;
        this.size = size;
        this.direction = direction;
        this.pair = pair;
        this.stopLevel = stopLevel.abs();
        this.limitLevel = limitLevel.abs();
        this.openTime = openPrice.time();
        this.openPrice = openPrice;
        this.positionType = positionType;
        this.margin = margin;
    }

    private void checkLevels(Direction direction, Decimal stopLevel, Decimal limitLevel) {
        if (direction == Direction.BUY && stopLevel.isGreaterThan(limitLevel)) {
            throw new IllegalArgumentException("For Buy Positions the stop level must be greater than the limit level");
        }

        if (direction == Direction.SELL && stopLevel.isLessThan(limitLevel)) {
            throw new IllegalArgumentException("For Sell Positions the stop level must be less than the limit level");
        }
    }

    public abstract void adapt(TimeSeriesEntry currentPrice);

    public TradingCalculator.ProfitLossResult currentProfit(TimeSeriesEntry currentPrice) {
        Decimal closePrice = currentPrice(currentPrice);
        Decimal profitPerPoint = profitPerPoint();
        return tradingCalculator.calcProfitLoss(getOpenPriceAsNumber(), closePrice, currentPrice.time(), direction, profitPerPoint);
    }

    public Decimal profitPerPoint() {
        return size.multiply(pair.profitPerPointForOneContract()).multiply(pair.sizeMultiplication());
    }

    public Decimal currentPrice(TimeSeriesEntry currentPrice) {
        return switch (direction) {
            case BUY -> currentPrice.closeBid();
            case SELL -> currentPrice.closeAsk();
        };
    }

    public boolean isTpReached(TimeSeriesEntry currentPrice) {
        Decimal currentTpPrice = currentTpPrice(currentPrice);
        return switch (direction) {
            case BUY -> currentTpPrice.isGreaterThanOrEqualTo(limitLevel);
            case SELL -> currentTpPrice.isLessThanOrEqualTo(limitLevel);
        };
    }


    public boolean isSlReached(TimeSeriesEntry currentPrice) {
        Decimal currentSlPrice = currentSlPrice(currentPrice);
        return switch (direction) {
            case BUY -> currentSlPrice.isLessThanOrEqualTo(stopLevel);
            case SELL -> currentSlPrice.isGreaterThanOrEqualTo(stopLevel);
        };
    }

    private Decimal currentTpPrice(TimeSeriesEntry currentPrice) {
        return switch (direction) {
            case BUY -> currentPrice.closeBid().max(currentPrice.highBid());
            case SELL -> currentPrice.closeAsk().min(currentPrice.lowAsk());
        };
    }


    protected Decimal currentSlPrice(TimeSeriesEntry currentPrice) {
        return switch (direction) {
            case BUY -> currentPrice.closeBid().min(currentPrice.lowBid());
            case SELL -> currentPrice.closeAsk().max(currentPrice.highAsk());
        };
    }


    public Decimal getMargin() {
        return margin;
    }

    public Decimal getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public Pair getPair() {
        return pair;
    }

    public Decimal getOpenPriceAsNumber() {
        return getOpenPriceAsNumber(direction, openPrice);
    }

    public TimeSeriesEntry getOpenPrice() {
        return openPrice;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public Decimal getStopLevel() {
        return stopLevel;
    }

    public Decimal getLimitLevel() {
        return limitLevel;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Position position = (Position) object;
        return Objects.equals(size, position.size) && direction == position.direction && Objects.equals(pair, position.pair) && Objects.equals(openPrice, position.openPrice) && Objects.equals(openTime, position.openTime) && Objects.equals(stopLevel, position.stopLevel) && Objects.equals(limitLevel, position.limitLevel) && positionType == position.positionType && Objects.equals(margin, position.margin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, direction, pair, openPrice, openTime, stopLevel, limitLevel, positionType, margin);
    }
}
