package com.becker.freelance.trading.external.services.candles;

import java.time.LocalDateTime;

public interface Synchronizeable {

    void synchronize(LocalDateTime time);
}
