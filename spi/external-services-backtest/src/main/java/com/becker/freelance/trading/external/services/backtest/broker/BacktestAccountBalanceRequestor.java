package com.becker.freelance.trading.external.services.backtest.broker;

import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.trading.external.services.broker.AccountBalanceRequestor;

public interface BacktestAccountBalanceRequestor extends AccountBalanceRequestor {

    public void setWallet(Wallet wallet);
}
