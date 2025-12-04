package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.Direction;

import java.util.Objects;

public final class ExitSignal {


    public static final ExitSignal CLOSE_ALL_BUY = new ExitSignal(Direction.BUY);

    public static final ExitSignal CLOSE_ALL_SELL = new ExitSignal(Direction.SELL);
    private final Direction directionToClose;

    public ExitSignal(Direction directionToClose) {
        this.directionToClose = directionToClose;
    }

    public Direction directionToClose() {
        return directionToClose;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ExitSignal) obj;
        return Objects.equals(this.directionToClose, that.directionToClose);
    }

    @Override
    public int hashCode() {
        return Objects.hash(directionToClose);
    }

    @Override
    public String toString() {
        return "ExitSignal[" +
                "directionToClose=" + directionToClose + ']';
    }


}
