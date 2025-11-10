package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.trading.external.services.candles.Synchronizeable;
import com.becker.freelance.trading.external.services.registry.ExternalService;

import java.time.LocalDateTime;

public interface Synchronizer extends ExternalService {

    void addSubscriber(Synchronizeable synchronizeable);

    void addPrioritySubscriber(Synchronizeable synchronizeable);

    LocalDateTime minTime();
}
