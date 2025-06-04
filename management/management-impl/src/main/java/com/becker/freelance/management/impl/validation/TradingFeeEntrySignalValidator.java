package com.becker.freelance.management.impl.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.validation.EntrySignalValidator;
import com.becker.freelance.math.Decimal;

public class TradingFeeEntrySignalValidator implements EntrySignalValidator {

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {

        Decimal tradingFee = calculateTradingFee(environmentProvider, entrySignal);
        Decimal targetProfit = entrySignal.targetProfit();

        return targetProfit.isGreaterThan(tradingFee);
    }

    private Decimal calculateTradingFee(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        Decimal tradingFee = Decimal.ZERO;
        if (entrySignal.isOpenTaker()) {
            tradingFee = tradingFee.add(environmentProvider.calculateTakerTradingFeeInCounterCurrency(entrySignal.openPrice().getCloseMid(), entrySignal.size()));
        } else {
            tradingFee = tradingFee.add(environmentProvider.calculateMakerTradingFeeInCounterCurrency(entrySignal.openPrice().getCloseMid(), entrySignal.size()));
        }

        if (entrySignal.isCloseTaker()) {
            tradingFee = tradingFee.add(environmentProvider.calculateTakerTradingFeeInCounterCurrency(entrySignal.limitLevel(), entrySignal.size()));
        } else {
            tradingFee = tradingFee.add(environmentProvider.calculateMakerTradingFeeInCounterCurrency(entrySignal.limitLevel(), entrySignal.size()));
        }

        return tradingFee;
    }
}
