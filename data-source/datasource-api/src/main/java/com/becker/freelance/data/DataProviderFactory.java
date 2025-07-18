package com.becker.freelance.data;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.service.ExtServiceLoader;

import java.util.List;

public abstract class DataProviderFactory {

    public static DataProviderFactory find(AppMode appMode) {
        List<DataProviderFactory> factories = ExtServiceLoader.loadMultiple(DataProviderFactory.class)
                .filter(provider -> provider.supports(appMode)).toList();

        if (factories.size() > 1) {
            throw new IllegalStateException("Found multiple DataProvider for AppMode " + appMode.getDescription() + ": " + factories);
        }
        if (factories.isEmpty()) {
            throw new IllegalArgumentException("AppMode " + appMode + " is not supported");
        }

        return factories.get(0);
    }

    protected abstract boolean supports(AppMode appMode);

    public abstract SubscribableDataProvider createSubscribableDataProvider(Pair pair, Synchronizer synchronizer);

    public SubscribableDataProvider createSubscribableDataProvider(Pair pair) {
        throw new UnsupportedOperationException("Synchronizer needed for construction");
    }

    public abstract DataProvider createDataProvider(Pair pair);

    public EurUsdRequestor createEurUsdRequestor() {
        return new DefaultEurUsdRequestor(this);
    }


    public EurUsdRequestor createEurUsdRequestor(Synchronizer synchronizer) {
        return new DefaultEurUsdRequestor(this, synchronizer);
    }
}
