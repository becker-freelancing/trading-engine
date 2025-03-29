package com.becker.freelance.commons.wallet;

import com.becker.freelance.math.Decimal;

public interface Wallet {
    void adjustAmount(Decimal profit);

    void removeMargin(Decimal margin);

    boolean canOpen(Decimal margin);

    void addMargin(Decimal margin);

    Decimal getMargin();

    Decimal getAmount();
}
