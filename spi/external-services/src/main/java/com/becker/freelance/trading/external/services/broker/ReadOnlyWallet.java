package com.becker.freelance.trading.external.services.broker;

import com.becker.freelance.math.Decimal;

public interface ReadOnlyWallet {

    public Decimal getAmount();

    public Decimal getMargin();
}
