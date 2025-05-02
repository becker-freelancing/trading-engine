package com.becker.freelance.tradeexecution;

import com.becker.freelance.math.Decimal;

public interface ReadOnlyWallet {

    public Decimal getAmount();

    public Decimal getMargin();
}
