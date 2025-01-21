package com.becker.freelance.commons;


import com.becker.freelance.commons.calculation.TradingCalculator;

import java.time.LocalDateTime;

public abstract class Position {
    protected double size;
    protected Direction direction;
    protected Pair pair;
    protected double openPrice;
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
        this.openTime = openPrice.getTime();
        this.openPrice = direction == Direction.BUY ? openPrice.getCloseAsk() : openPrice.getCloseBid();
        this.positionType = positionType;
        this.margin = margin;
    }

    public abstract void adapt(TimeSeriesEntry currentPrice);

    public TradingCalculator.ProfitLossResult currentProfit(TimeSeriesEntry currentPrice, TradingCalculator tradingCalculator) {
        double price = currentPrice(currentPrice);
        double profitPerPoint = profitPerPoint();
        return tradingCalculator.calcProfitLoss(openPrice, price, currentPrice.getTime(), direction, profitPerPoint);
    }

    public double profitPerPoint() {
        return size * pair.getProfitPerPointForOneContract();
    }

    public double currentPrice(TimeSeriesEntry currentPrice) {
        return direction == Direction.BUY ? currentPrice.getCloseBid() : currentPrice.getCloseAsk();
    }

    public boolean isTpReached(TimeSeriesEntry currentPrice) {
        double priceDifference = currentPrice(currentPrice) - openPrice;
        if (direction == Direction.BUY) {
            return priceDifference >= limitInPoints;
        } else {
            return priceDifference <= -limitInPoints;
        }
    }

    public boolean isSlReached(TimeSeriesEntry currentPrice) {
        double priceDifference = currentPrice(currentPrice) - openPrice;
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

    public double getOpenPrice() {
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
}
