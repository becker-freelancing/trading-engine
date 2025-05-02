package com.becker.freelance.opentrades;

import com.becker.freelance.math.Decimal;

public interface ReadOnlyWallet {

    public Decimal getAmount();

    public Decimal getMargin();
}
