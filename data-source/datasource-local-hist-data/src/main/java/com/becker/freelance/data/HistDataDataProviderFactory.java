package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.plugin.HistDataDemoAppMode;

public class HistDataDataProviderFactory extends DataProviderFactory {

    @Override
    protected boolean supports(AppMode appMode) {
        return new HistDataDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        return new HistDataSubscribableDataProvider(pair, synchronizer);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        return new HistDataDataProvider(pair);
    }
}
