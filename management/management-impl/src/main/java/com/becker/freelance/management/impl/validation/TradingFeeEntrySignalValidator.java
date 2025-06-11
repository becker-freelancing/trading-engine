package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.math.Decimal;

public class TradingFeeEntrySignalValidator implements EntrySignalValidator {

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {

        Decimal tradingFee = calculateTradingFee(environmentProvider, entrySignal);
        Decimal targetProfit = entrySignal.estimatedTargetProfit(environmentProvider.getCurrentPrice(entrySignal.pair()));

        return targetProfit.isGreaterThan(tradingFee);
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
