package com.becker.freelance.broker;

import com.becker.freelance.bybit.broker.BrokerController;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.opentrades.ReadOnlyWallet;

public class BybitBrokerRequestor extends BrokerRequestor {

    private final BrokerController brokerController;

    public BybitBrokerRequestor() {
        brokerController = new BrokerController();
    }


    @Override
    public ReadOnlyWallet getWallet() {
        return new ReadOnlyWallet() {
            @Override
            public Decimal getAmount() {
                return brokerController.getAmount();
            }

            @Override
            public Decimal getMargin() {
                return brokerController.getMargin();
            }
        };
    }

    @Override
    protected void setWallet(Wallet wallet) {

    }

    @Override
    public Integer getMaxBrokerFractionPlaces() {
        return 2;
    }
}
