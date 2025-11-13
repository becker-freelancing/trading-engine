package com.becker.freelance.trading.external.services.fees;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.trading.external.services.candles.PriceRequestorBroker;

public record TradingFeeCalculatorBuilderParams(PriceRequestorBroker priceRequestorBroker,
                                                EurUsdRequestor eurUsdRequestor) {
}
