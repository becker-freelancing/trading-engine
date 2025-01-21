package com.becker.freelance.commons;

public class Wallet {
    private double amount;
    private double margin;

    public Wallet(double initialAmount) {
        this.amount = initialAmount;
        this.margin = 0;
    }

    public Wallet() {
        this(2000);
    }

    public double getAvailableAmount() {
        return amount - margin;
    }

    public boolean canOpen(double requiredMargin) {
        return getAvailableAmount() >= requiredMargin;
    }

    public void addMargin(double margin) {
        this.margin += margin;
    }

    public void removeMargin(double margin) {
        this.margin -= margin;
    }

    public void adjustAmount(double diff) {
        this.amount += diff;
    }

    public double getAmount() {
        return amount;
    }

    public double getMargin() {
        return margin;
    }
}
