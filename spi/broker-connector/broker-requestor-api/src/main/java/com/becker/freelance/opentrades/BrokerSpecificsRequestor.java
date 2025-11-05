package com.becker.freelance.opentrades;


import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;

public interface BrokerSpecificsRequestor {

    public Integer getMaxBrokerFractionPlaces();

    public TradingCalculator getTradingCalculator(EurUsdRequestor eurUsdRequestor);
}
