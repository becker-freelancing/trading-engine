package com.becker.freelance.tradeexecution.calculation;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class MarginCalculatorImpl implements MarginCalculator {

    private final EurUsdRequestor eurUsd;

    public MarginCalculatorImpl(EurUsdRequestor eurUsd) {
        this.eurUsd = eurUsd;
    }

    @Override
    public Decimal getMarginCounterCurrency(Pair pair, Decimal size, Decimal openPrice) {
        return size
                .multiply(pair.leverageFactor())
                .multiply(openPrice)
                .multiply(pair.sizeMultiplication());
    }

    @Override
    public Decimal getMarginEur(Pair pair, Decimal size, Decimal openPrice, LocalDateTime time) {
        Decimal marginCounterCurrency = getMarginCounterCurrency(pair, size, openPrice);
        if (pair.isEuroCounterCurrency()) {
            return marginCounterCurrency.round(2);
        }

        return marginCounterCurrency
                .divide(eurUsd.getEurUsdForTime(time).getCloseMid())
                .round(2);
    }
}
