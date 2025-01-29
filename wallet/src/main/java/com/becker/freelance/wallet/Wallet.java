package com.becker.freelance.wallet;

import com.becker.freelance.math.Decimal;

public class Wallet {
    private Decimal amount;
    private Decimal margin;

    public Wallet(Decimal initialAmount) {
        this.amount = initialAmount;
        this.margin = Decimal.ZERO;
    }


    public Decimal getAvailableAmount() {
        return amount.subtract(margin);
    }

    public boolean canOpen(Decimal requiredMargin) {
        return getAvailableAmount().isGreaterThanOrEqualTo(requiredMargin);
    }

    public void addMargin(Decimal margin) {
        this.margin = this.margin.add(margin);
    }

    public void removeMargin(Decimal margin) {
        this.margin = this.margin.subtract(margin);
    }

    public void adjustAmount(Decimal diff) {
        this.amount = this.amount.add(diff);
    }

    public Decimal getAmount() {
        return amount;
    }

    public Decimal getMargin() {
        return margin;
    }
}
