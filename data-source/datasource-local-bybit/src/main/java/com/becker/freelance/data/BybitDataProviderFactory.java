package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.plugin.BybitDemoAppMode;

public class BybitDataProviderFactory extends DataProviderFactory {

    @Override
    protected boolean supports(AppMode appMode) {
        return new BybitDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        return new BybitSubscribableDataProvider(pair, synchronizer);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        return new BybitDataProvider(pair);
    }
}
