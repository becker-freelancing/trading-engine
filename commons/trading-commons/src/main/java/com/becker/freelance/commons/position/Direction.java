package com.becker.freelance.commons.position;

public enum Direction {
    BUY(1),
    SELL(-1);

    private final int factor;

    Direction(int factor) {
        this.factor = factor;
    }

    public int getFactor() {
        return factor;
    }

    public Direction negate() {
        return switch (this) {
            case BUY -> SELL;
            case SELL -> BUY;
        };
    }
}

