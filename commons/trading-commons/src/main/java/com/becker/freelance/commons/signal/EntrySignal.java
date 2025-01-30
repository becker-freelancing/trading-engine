package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.math.Decimal;

public class EntrySignal {

    private Decimal size;
    private Direction direction;
    private Decimal stopInEuros;
    private Decimal limitInEuros;
    private PositionType positionType;
    private Decimal trailingStepSize;

    public EntrySignal(Decimal size, Direction direction, Decimal stopInEuro, Decimal limitInEuro,
                       PositionType positionType) {
        this.size = size;
        this.direction = direction;
        this.stopInEuros = stopInEuro;
        this.limitInEuros = limitInEuro;
        this.positionType = positionType;
    }

    public EntrySignal(Decimal size, Direction direction, Decimal stopInEuros, Decimal limitInEuros,
                       PositionType positionType, Decimal trailingStepSize) {
        this(size, direction, stopInEuros, limitInEuros, positionType);
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

    public Decimal getStopInEuros() {
        return stopInEuros;
    }

    public void setStopInEuros(Decimal stopInEuros) {
        this.stopInEuros = stopInEuros;
    }

    public Decimal getLimitInEuros() {
        return limitInEuros;
    }

    public void setLimitInEuros(Decimal limitInEuros) {
        this.limitInEuros = limitInEuros;
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

