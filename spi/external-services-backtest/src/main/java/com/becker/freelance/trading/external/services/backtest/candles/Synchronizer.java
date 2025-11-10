package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.trading.external.services.candles.Synchronizeable;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface Synchronizer extends ExternalService {

    void addSubscriber(Synchronizeable synchronizeable);

    void addPrioritySubscriber(Synchronizeable synchronizeable);

}
