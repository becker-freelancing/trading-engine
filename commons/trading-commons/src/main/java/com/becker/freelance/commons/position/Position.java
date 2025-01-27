package com.becker.freelance.commons.position;


import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Position {
    protected double size;
    protected Direction direction;
    protected Pair pair;
    protected TimeSeriesEntry openPrice;
    protected LocalDateTime openTime;
    protected double stopInPoints;
    protected double limitInPoints;
    protected PositionType positionType;
    protected double margin;

    public Position(double size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                    double stopInPoints, double limitInPoints, PositionType positionType, double margin) {
        this.size = size;
        this.direction = direction;
        this.pair = pair;
        this.stopInPoints = Math.abs(stopInPoints);
        this.limitInPoints = Math.abs(limitInPoints);
        this.openTime = openPrice.time();
        this.openPrice = openPrice;
        this.positionType = positionType;
        this.margin = margin;
    }

    public abstract void adapt(TimeSeriesEntry currentPrice);

    public TradingCalculator.ProfitLossResult currentProfit(TimeSeriesEntry currentPrice, TradingCalculator tradingCalculator) {
        double price = currentPrice(currentPrice);
        double profitPerPoint = profitPerPoint();
        return tradingCalculator.calcProfitLoss(getOpenPriceAsNumber(), price, currentPrice.time(), direction, profitPerPoint);
    }

    public double profitPerPoint() {
        return size * pair.profitPerPointForOneContract();
    }

    public double currentPrice(TimeSeriesEntry currentPrice) {
        return direction == Direction.BUY ? currentPrice.closeBid() : currentPrice.closeAsk();
    }

    public boolean isTpReached(TimeSeriesEntry currentPrice) {
        double priceDifference = currentPrice(currentPrice) - getOpenPriceAsNumber();
        if (direction == Direction.BUY) {
            return priceDifference >= limitInPoints;
        } else {
            return priceDifference <= -limitInPoints;
        }
    }

    public boolean isSlReached(TimeSeriesEntry currentPrice) {
        double priceDifference = currentPrice(currentPrice) - getOpenPriceAsNumber();
        if (direction == Direction.BUY) {
            return priceDifference <= -stopInPoints;
        } else {
            return priceDifference >= stopInPoints;
        }
    }

    public double getMargin() {
        return margin;
    }

    public double getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public Pair getPair() {
        return pair;
    }

    public double getOpenPriceAsNumber() {
        return direction == Direction.BUY ? openPrice.closeAsk() : openPrice.closeBid();
    }

    public TimeSeriesEntry getOpenPrice() {
        return openPrice;
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public double getStopInPoints() {
        return stopInPoints;
    }

    public double getLimitInPoints() {
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
        return Double.compare(size, position.size) == 0 && Double.compare(stopInPoints, position.stopInPoints) == 0 && Double.compare(limitInPoints, position.limitInPoints) == 0 && Double.compare(margin, position.margin) == 0 && direction == position.direction && Objects.equals(pair, position.pair) && Objects.equals(openPrice, position.openPrice) && Objects.equals(openTime, position.openTime) && positionType == position.positionType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, direction, pair, openPrice, openTime, stopInPoints, limitInPoints, positionType, margin);
    }
}
