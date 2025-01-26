package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;

public class EntrySignal {

    private double amount;
    private Direction direction;
    private double stopInPoints;
    private double limitInPoints;
    private PositionType positionType;
    private Double trailingStepSize;

    public EntrySignal(double amount, Direction direction, double stopInPoints, double limitInPoints,
                       PositionType positionType) {
        this.amount = amount;
        this.direction = direction;
        this.stopInPoints = stopInPoints;
        this.limitInPoints = limitInPoints;
        this.positionType = positionType;
    }

    public EntrySignal(double amount, Direction direction, double stopInPoints, double limitInPoints,
                       PositionType positionType, Double trailingStepSize) {
        this(amount, direction, stopInPoints, limitInPoints, positionType);
        this.trailingStepSize = trailingStepSize;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getStopInPoints() {
        return stopInPoints;
    }

    public void setStopInPoints(double stopInPoints) {
        this.stopInPoints = stopInPoints;
    }

    public double getLimitInPoints() {
        return limitInPoints;
    }

    public void setLimitInPoints(double limitInPoints) {
        this.limitInPoints = limitInPoints;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
    }

    public Double getTrailingStepSize() {
        return trailingStepSize;
    }

    public void setTrailingStepSize(Double trailingStepSize) {
        this.trailingStepSize = trailingStepSize;
    }
}

