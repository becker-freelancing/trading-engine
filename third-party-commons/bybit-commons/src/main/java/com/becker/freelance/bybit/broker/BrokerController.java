package com.becker.freelance.bybit.broker;

import com.becker.freelance.math.Decimal;

public class BrokerController {

    private final BrokerApiClient brokerApiClient;

    public BrokerController() {
        this.brokerApiClient = new BrokerApiClient();
    }

    public Decimal getMargin() {
        return brokerApiClient.getMargin();
    }

    public Decimal getAmount() {
        return brokerApiClient.getAmount();
    }
}
