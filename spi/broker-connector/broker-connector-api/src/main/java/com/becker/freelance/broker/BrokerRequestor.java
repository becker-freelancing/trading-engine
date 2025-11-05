package com.becker.freelance.broker;

import com.becker.freelance.commons.service.ExtServiceLoader;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.opentrades.AccountBalanceRequestor;
import com.becker.freelance.opentrades.BrokerSpecificsRequestor;
import com.becker.freelance.tradeexecution.TradeExecutor;

import java.util.Optional;

public abstract class BrokerRequestor implements AccountBalanceRequestor, BrokerSpecificsRequestor {

    public static BrokerRequestor find(TradeExecutor tradeExecutor) {

        Optional<Wallet> wallet = tradeExecutor.getWallet();

        BrokerRequestor brokerRequestor = ExtServiceLoader.loadSingle(BrokerRequestor.class);
        wallet.ifPresent(brokerRequestor::setWallet);
        return brokerRequestor;
    }

    protected abstract void setWallet(Wallet wallet);
}
