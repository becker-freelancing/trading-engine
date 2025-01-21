package com.becker.freelance.commons;

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
}

