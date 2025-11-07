package com.becker.freelance.trading.external.services.backtest.candles;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.trading.external.services.registry.ExternalService;

import java.time.LocalDateTime;

public interface BacktestDataTotalTimeProvider extends ExternalService {

    public LocalDateTime getAbsoluteMinTime(Pair pair);

    public LocalDateTime getAbsoluteMaxTime(Pair pair);
}
