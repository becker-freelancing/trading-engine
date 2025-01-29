package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.math.Decimal;

public class EntrySignal {

    private Decimal size;
    private Direction direction;
    private Decimal stopInPoints;
    private Decimal limitInPoints;
    private PositionType positionType;
    private Decimal trailingStepSize;

    public EntrySignal(Decimal size, Direction direction, Decimal stopInPoints, Decimal limitInPoints,
                       PositionType positionType) {
        this.size = size;
        this.direction = direction;
        this.stopInPoints = stopInPoints;
        this.limitInPoints = limitInPoints;
        this.positionType = positionType;
    }

    public EntrySignal(Decimal size, Direction direction, Decimal stopInPoints, Decimal limitInPoints,
                       PositionType positionType, Decimal trailingStepSize) {
        this(size, direction, stopInPoints, limitInPoints, positionType);
        this.trailingStepSize = trailingStepSize;
    }

    public Decimal getSize() {
        return size;
    }

    public void setSize(Decimal size) {
        this.size = size;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Decimal getStopInPoints() {
        return stopInPoints;
    }

    public void setStopInPoints(Decimal stopInPoints) {
        this.stopInPoints = stopInPoints;
    }

    public Decimal getLimitInPoints() {
        return limitInPoints;
    }

    public void setLimitInPoints(Decimal limitInPoints) {
        this.limitInPoints = limitInPoints;
    }

    public PositionType getPositionType() {
        return positionType;
    }

    public void setPositionType(PositionType positionType) {
        this.positionType = positionType;
    }

    public Decimal getTrailingStepSize() {
        return trailingStepSize;
    }

    public void setTrailingStepSize(Decimal trailingStepSize) {
        this.trailingStepSize = trailingStepSize;
    }
}

