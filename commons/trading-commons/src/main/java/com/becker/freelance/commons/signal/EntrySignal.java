package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;

public class EntrySignal {

    private double size;
    private Direction direction;
    private double stopInPoints;
    private double limitInPoints;
    private PositionType positionType;
    private Double trailingStepSize;

    public EntrySignal(double size, Direction direction, double stopInPoints, double limitInPoints,
                       PositionType positionType) {
        this.size = size;
        this.direction = direction;
        this.stopInPoints = stopInPoints;
        this.limitInPoints = limitInPoints;
        this.positionType = positionType;
    }

    public EntrySignal(double size, Direction direction, double stopInPoints, double limitInPoints,
                       PositionType positionType, Double trailingStepSize) {
        this(size, direction, stopInPoints, limitInPoints, positionType);
        this.trailingStepSize = trailingStepSize;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
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

