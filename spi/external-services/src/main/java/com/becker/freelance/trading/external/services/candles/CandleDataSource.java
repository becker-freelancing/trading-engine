package com.becker.freelance.trading.external.services.candles;

import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface CandleDataSource extends ExternalService, Synchronizeable {

    public abstract void addSubscriber(DataSubscriber subscriber);
}
