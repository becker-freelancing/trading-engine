package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.plugin.CapitalDemoAppMode;

public class CapitalDataProviderFactory extends DataProviderFactory {

    @Override
    protected boolean supports(AppMode appMode) {
        return new CapitalDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        throw new UnsupportedOperationException("No DataProvider available for Capital.com");
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair) {
        return new CapitalSubscribableDataProvider(pair);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        throw new UnsupportedOperationException("No DataProvider available for Capital.com");
    }
}
