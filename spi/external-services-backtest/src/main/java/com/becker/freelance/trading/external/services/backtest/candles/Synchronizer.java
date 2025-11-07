package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.trading.external.services.candles.Synchronizeable;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Synchronizer {

    void addSubscriber(Synchronizeable synchronizeable);

    void addPrioritySubscriber(Synchronizeable synchronizeable);

    Optional<LocalDateTime> minTime();

    Optional<LocalDateTime> maxTime();
}
