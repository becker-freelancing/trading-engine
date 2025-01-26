package com.becker.freelance.commons.signal;

public class ExitSignal {

    private double amount;
    private Direction directionToClose;

    public ExitSignal(double amount, Direction directionsToClose) {
        this.amount = amount;
        this.directionToClose = directionsToClose;
    }

    public double getAmount() {
        return amount;
    }

    public Direction getDirectionToClose() {
        return directionToClose;
    }
}
