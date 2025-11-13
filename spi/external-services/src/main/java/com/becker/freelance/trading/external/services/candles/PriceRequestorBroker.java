package com.becker.freelance.trading.external.services.candles;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface PriceRequestorBroker extends ExternalService {

    public PriceRequestor forPair(Pair pair);
}
