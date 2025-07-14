package com.becker.freelance.data;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.plugin.BybitLocalDemoAppMode;

public class BybitDataProviderFactory extends DataProviderFactory {

    @Override
    protected boolean supports(AppMode appMode) {
        return new BybitLocalDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        return new BybitSubscribableDataProvider(pair, synchronizer);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        return new BybitDataProvider(pair);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair) {
        TestSynchronizer synchronizer = new TestSynchronizer();
        BybitSubscribableDataProvider bybitSubscribableDataProvider = new BybitSubscribableDataProvider(pair, synchronizer);
        synchronizer.set(bybitSubscribableDataProvider);
        synchronizer.start();
        return bybitSubscribableDataProvider;
    }
}
