package com.becker.freelance.trading.external.services.broker;


import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface BrokerSpecificsRequestor extends ExternalService {

    public Integer getMaxBrokerFractionPlaces();

    public TradingCalculator getTradingCalculator(EurUsdRequestor eurUsdRequestor);

    boolean doesOrderTypeAffectExecutionCost();
}
