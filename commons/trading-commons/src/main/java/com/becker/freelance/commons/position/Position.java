package com.becker.freelance.commons.position;


import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.Objects;
import com.becker.freelance.math.Decimal;
import org.ta4j.core.Bar;

public abstract class Position {
    protected Decimal size;
    protected Direction direction;
    protected Pair pair;
    protected TimeSeriesEntry openPrice;
    protected LocalDateTime openTime;
    protected Decimal stopInPoints;
    protected Decimal limitInPoints;
    protected PositionType positionType;
    protected Decimal margin;

    public Position(Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                    Decimal stopInPoints, Decimal limitInPoints, PositionType positionType, Decimal margin) {
        this.size = size;
        this.direction = direction;
        this.pair = pair;
        this.stopInPoints = stopInPoints.abs();
        this.limitInPoints = limitInPoints.abs();
        this.openTime = openPrice.time();
        this.openPrice = openPrice;
        this.positionType = positionType;
        this.margin = margin;
    }

    public abstract void adapt(TimeSeriesEntry currentPrice);

    public TradingCalculator.ProfitLossResult currentProfit(TimeSeriesEntry currentPrice, TradingCalculator tradingCalculator) {
        Decimal price = currentPrice(currentPrice);
        Decimal profitPerPoint = profitPerPoint();
        return tradingCalculator.calcProfitLoss(getOpenPriceAsNumber(), price, currentPrice.time(), direction, profitPerPoint);
    }

    public Decimal profitPerPoint() {
        return size.multiply(pair.profitPerPointForOneContract());
    }

    public Decimal currentPrice(TimeSeriesEntry currentPrice) {
        return switch (direction){
            case BUY -> currentPrice.closeBid();
            case SELL -> currentPrice.closeAsk();
        };
    }

    public boolean isTpReached(TimeSeriesEntry currentPrice) {
        Decimal priceDifference = currentTpPrice(currentPrice).subtract(getOpenPriceAsNumber());
        return switch (direction) {
            case BUY ->  priceDifference.isGreaterThan(limitInPoints) || priceDifference.isEqualTo(limitInPoints);
            case SELL -> priceDifference.isLessThan(limitInPoints.negate()) || priceDifference.isEqualTo(limitInPoints.negate());
        };
    }

    private Decimal currentTpPrice(TimeSeriesEntry currentPrice) {
        return switch (direction){
            case BUY -> currentPrice.closeBid().max(currentPrice.highBid());
            case SELL -> currentPrice.closeAsk().min(currentPrice.lowAsk());
        };
    }


    private Decimal currentSlPrice(TimeSeriesEntry currentPrice) {
        return switch (direction){
            case BUY -> currentPrice.closeBid().min(currentPrice.lowBid());
            case SELL -> currentPrice.closeAsk().max(currentPrice.highAsk());
        };
    }

    public boolean isSlReached(TimeSeriesEntry currentPrice) {
        Decimal priceDifference = currentSlPrice(currentPrice).subtract(getOpenPriceAsNumber());
        return switch (direction) {
            case BUY -> priceDifference.isLessThan(stopInPoints.negate()) || priceDifference.isEqualTo(stopInPoints.negate());
            case SELL -> priceDifference.isGreaterThan(stopInPoints) || priceDifference.isEqualTo(stopInPoints);
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
        return switch (direction){
            case BUY -> openPrice.closeAsk();
            case SELL -> openPrice.closeBid();
        };
    }

    public TimeSeriesEntry getOpenPrice() {
        return openPrice;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public Decimal getStopInPoints() {
        return stopInPoints;
    }

    public Decimal getLimitInPoints() {
        return limitInPoints;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Position position = (Position) object;
        return Objects.equals(size, position.size) && direction == position.direction && Objects.equals(pair, position.pair) && Objects.equals(openPrice, position.openPrice) && Objects.equals(openTime, position.openTime) && Objects.equals(stopInPoints, position.stopInPoints) && Objects.equals(limitInPoints, position.limitInPoints) && positionType == position.positionType && Objects.equals(margin, position.margin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, direction, pair, openPrice, openTime, stopInPoints, limitInPoints, positionType, margin);
    }
}
