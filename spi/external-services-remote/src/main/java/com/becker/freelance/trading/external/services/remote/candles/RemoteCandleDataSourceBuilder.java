package com.becker.freelance.trading.external.services.remote.candles;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.trading.external.services.candles.CandleDataSourceBuilder;

public interface RemoteCandleDataSourceBuilder extends CandleDataSourceBuilder<RemoteCandleSourceBuilderParams, RemoteCandleDataSource> {

    public EurUsdRequestor createEuroUsdRequestor();
}
