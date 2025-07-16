package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.math.Decimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradingFeeEntrySignalValidator implements EntrySignalValidator {

    private static final Logger logger = LoggerFactory.getLogger(TradingFeeEntrySignalValidator.class);

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {

        Decimal tradingFee = calculateTradingFee(environmentProvider, entrySignal);
        Decimal targetProfit = entrySignal.estimatedTargetProfit(environmentProvider.getCurrentPrice(entrySignal.pair()));

        boolean valid = targetProfit.isGreaterThan(tradingFee);
        logger.debug("Trading Fee is valid: {}. Fee: {}, Target Profit: {}", valid, tradingFee, targetProfit);
        return valid;
    }

    private Decimal calculateTradingFee(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        Decimal openLevel = entrySignal.estimatedOpenLevel(environmentProvider.getCurrentPrice(entrySignal.pair()));
        Decimal limitLevel = entrySignal.estimatedLimitLevel(environmentProvider.getCurrentPrice(entrySignal.pair()));

        Decimal tradingFee = Decimal.ZERO;
        if (entrySignal.isOpenTaker()) {
            tradingFee = tradingFee.add(environmentProvider.calculateTakerTradingFeeInCounterCurrency(openLevel, entrySignal.size()));
        } else {
            tradingFee = tradingFee.add(environmentProvider.calculateMakerTradingFeeInCounterCurrency(openLevel, entrySignal.size()));
        }

        if (entrySignal.isOneCloseTaker()) {
            tradingFee = tradingFee.add(environmentProvider.calculateTakerTradingFeeInCounterCurrency(limitLevel, entrySignal.size()));
        } else {
            tradingFee = tradingFee.add(environmentProvider.calculateMakerTradingFeeInCounterCurrency(limitLevel, entrySignal.size()));
        }

        return tradingFee;
    }
}
