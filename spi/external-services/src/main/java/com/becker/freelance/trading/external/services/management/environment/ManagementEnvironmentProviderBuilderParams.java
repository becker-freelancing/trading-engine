package com.becker.freelance.trading.external.services.management.environment;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.PriceRequestor;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.trading.external.services.broker.AccountBalanceRequestor;
import com.becker.freelance.trading.external.services.broker.BrokerSpecificsRequestor;
import com.becker.freelance.trading.external.services.broker.ClosedTradesRequestor;
import com.becker.freelance.trading.external.services.broker.OpenPositionRequestor;

public record ManagementEnvironmentProviderBuilderParams(AccountBalanceRequestor accountBalanceRequestor,
                                                         BrokerSpecificsRequestor brokerSpecificsRequestor,
                                                         OpenPositionRequestor openPositionRequestor,
                                                         ClosedTradesRequestor closedTradesRequestor,
                                                         EurUsdRequestor eurUsdRequestor,
                                                         PriceRequestor priceRequestor,
                                                         TradingFeeCalculator tradingFeeCalculator) {
}
