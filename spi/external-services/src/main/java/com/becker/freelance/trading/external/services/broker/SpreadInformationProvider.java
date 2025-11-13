package com.becker.freelance.trading.external.services.broker;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface SpreadInformationProvider extends ExternalService {

    public Decimal getAverageSpreadForPair(Pair pair);

    public Decimal getMinimalSpreadForPair(Pair pair);
}
