package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.math.Decimal;

public abstract class EntrySignal {

    private Decimal size;
    private Direction direction;
    private final PositionType positionType;
    private final Decimal trailingStepSize;


    EntrySignal(Decimal size, Direction direction, PositionType positionType, Decimal trailingStepSize) {
        this.size = size;
        this.direction = direction;
        this.positionType = positionType;
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

    public PositionType getPositionType() {
        return positionType;
    }

    public Decimal getTrailingStepSize() {
        return trailingStepSize;
    }

}

