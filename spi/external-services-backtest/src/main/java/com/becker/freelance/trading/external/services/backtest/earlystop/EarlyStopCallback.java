package com.becker.freelance.trading.external.services.backtest.earlystop;

import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface EarlyStopCallback extends ExternalService {

    public EarlyStopCallbackResult shouldStop(CurrentBacktestState currentBacktestState);
}
