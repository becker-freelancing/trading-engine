package com.becker.freelance.trading.external.services.candles;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.trading.external.services.registry.ExternalService;
import com.becker.freelance.trading.external.services.registry.SupportableExternalServiceBuilder;

public interface CandleDataSourceBuilder<PARAMS, SERVICE extends ExternalService> extends SupportableExternalServiceBuilder<PARAMS, AppMode, SERVICE> {

    public EurUsdRequestor createEuroUsdRequestor();
}
