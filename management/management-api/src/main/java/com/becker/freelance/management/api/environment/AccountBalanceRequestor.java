package com.becker.freelance.management.api.environment;

import com.becker.freelance.math.Decimal;

public interface AccountBalanceRequestor {

    public Decimal getCurrentAccountBalance();
}
