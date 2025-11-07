package com.becker.freelance.trading.external.services.candles;

import com.becker.freelance.commons.calculation.PriceRequestor;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface CandleDataSource extends ExternalService, Synchronizeable, PriceRequestor {

    public abstract void addSubscriber(DataSubscriber subscriber);
}
