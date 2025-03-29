package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface MarginCalculator {

    public Decimal getMarginCounterCurrency(Pair pair, Decimal size, Decimal openPrice);

    public Decimal getMarginEur(Pair pair, Decimal size, Decimal openPrice, LocalDateTime time);
}
