package com.becker.freelance.data;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.plugin.KrakenDemoAppMode;

public class KrakenDataProviderFactory extends DataProviderFactory {
    @Override
    protected boolean supports(AppMode appMode) {
        return new KrakenDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        return new KrakenSubscribableDataProvider(pair, synchronizer);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        return new KrakenDataProvider(pair);
    }
}
