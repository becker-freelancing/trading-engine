package com.becker.freelance.data;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;

public class BybitDataProviderFactory extends DataProviderFactory {

    @Override
    protected boolean supports(AppMode appMode) {
        return false;
//        return new BybitDemoAppMode().equals(appMode);
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        throw new UnsupportedOperationException("No DataProvider available for ByBit");
    }

    @Override
    public SubscribableDataProvider createSubscribableDataProvider(Pair pair) {
        return new BybitSubscribableDataProvider(pair);
    }

    @Override
    public DataProvider createDataProvider(Pair pair) {
        throw new UnsupportedOperationException("No DataProvider available for ByBit");
    }

    @Override
    public EurUsdRequestor createEurUsdRequestor() {
        return new BybitEurUsdRequestor();
    }
}
