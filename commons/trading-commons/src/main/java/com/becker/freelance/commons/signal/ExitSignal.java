package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.math.Decimal;

public class ExitSignal {

    private final Decimal amount;
    private final Direction directionToClose;

    public ExitSignal(Decimal amount, Direction directionsToClose) {
        this.amount = amount;
        this.directionToClose = directionsToClose;
    }

    public ExitSignal(Direction directionToClose) {
        this(Decimal.DOUBLE_MAX, directionToClose);
    }

    public Decimal getAmount() {
        return amount;
    }

    public Direction getDirectionToClose() {
        return directionToClose;
    }
}
