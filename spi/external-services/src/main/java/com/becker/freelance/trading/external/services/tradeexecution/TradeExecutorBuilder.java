package com.becker.freelance.trading.external.services.tradeexecution;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.trading.external.services.registry.SupportableExternalServiceBuilder;

public interface TradeExecutorBuilder<PARAMS, SERVICE extends TradeExecutor> extends SupportableExternalServiceBuilder<PARAMS, AppMode, SERVICE> {
}
