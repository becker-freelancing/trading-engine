package com.becker.freelance.trading.external.services.management.environment;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.trading.external.services.broker.AccountBalanceRequestor;
import com.becker.freelance.trading.external.services.broker.BrokerSpecificsRequestor;
import com.becker.freelance.trading.external.services.broker.ClosedTradesRequestor;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;
import com.becker.freelance.trading.external.services.candles.PriceRequestorBroker;
import com.becker.freelance.trading.external.services.fees.TradingFeeCalculator;

public record ManagementEnvironmentProviderBuilderParams(AccountBalanceRequestor accountBalanceRequestor,
                                                         BrokerSpecificsRequestor brokerSpecificsRequestor,
                                                         OpenPositionRequestor openPositionRequestor,
                                                         ClosedTradesRequestor closedTradesRequestor,
                                                         EurUsdRequestor eurUsdRequestor,
                                                         PriceRequestorBroker priceRequestorBroker,
                                                         TradingFeeCalculator tradingFeeCalculator) {
}
