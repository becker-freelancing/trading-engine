package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.time.LocalDateTime;

public record CurrentPricePriceRequestor(TimeSeriesEntry currentPrice) implements PriceRequestor {
    @Override
    public TimeSeriesEntry getPriceForTime(Pair pair, LocalDateTime time) {
        if (!currentPrice.pair().equals(pair)) {
            throw new IllegalStateException("Different Pair requested");
        }
        if (!currentPrice.time().equals(time)) {
            throw new IllegalStateException("Different Time requested");
        }
        return currentPrice;
    }
}
