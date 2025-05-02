package com.becker.freelance.data;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.plugin.BinanceDemoAppMode;

public class BinanceDataProviderFactory extends DataProviderFactory {

    @Override
    protected boolean supports(AppMode appMode) {
        return new BinanceDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        return new BinanceSubscribableDataProvider(pair, synchronizer);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        return new BinanceDataProvider(pair);
    }
}
