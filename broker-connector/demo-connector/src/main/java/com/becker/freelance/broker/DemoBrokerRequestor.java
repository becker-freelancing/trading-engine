package com.becker.freelance.broker;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.calculation.TradingCalculatorImpl;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.ReadOnlyWallet;

public class DemoBrokerRequestor extends BrokerRequestor {

    private Wallet wallet;

    @Override
    public ReadOnlyWallet getWallet() {
        return new ReadOnlyWallet() {
            @Override
            public Decimal getAmount() {
                return wallet.getAmount();
            }

            @Override
            public Decimal getMargin() {
                return wallet.getMargin();
            }
        };
    }

    @Override
    protected void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    @Override
    public Integer getMaxBrokerFractionPlaces() {
        return 2;
    }

    @Override
    public TradingCalculator getTradingCalculator(EurUsdRequestor eurUsdRequestor) {
        return new TradingCalculatorImpl(eurUsdRequestor);
    }
}
