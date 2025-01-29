package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.function.Function;

public class MarginCalculator {

    private TimeSeries conversionRate;
    private Function<LocalDateTime, Decimal> conversionFactor;
    private Decimal leverageFactor;
    private Pair pair;

    private Decimal noopConversion(LocalDateTime time) {
        return Decimal.ONE;
    }

    private Decimal eurusdConversion(LocalDateTime time) {
        TimeSeriesEntry entry = conversionRate.getEntryForTime(time);
        return entry.getCloseMid();
    }

    public MarginCalculator(Pair pair, TimeSeries timeSeries) {
        if (!timeSeries.getPair().equals(Pair.eurUsd1())) {
            throw new IllegalArgumentException("No EUR/USD Time Series provided");
        }
        this.pair = pair;
        this.leverageFactor = pair.leverageFactor();

        if ("USD".equals(pair.counterCurrency())) {
            this.conversionRate = timeSeries;
            this.conversionFactor = this::eurusdConversion;
        } else if ("EUR".equals(pair.counterCurrency())) {
            this.conversionFactor = this::noopConversion;
        }
    }

    public Decimal calcMargin(Decimal size, TimeSeriesEntry openCourse) {
        Decimal marginCounterCurrency = size.multiply(leverageFactor).multiply(openCourse.getCloseMid()).multiply(pair.sizeMultiplication());
        Decimal conversionFactorValue = conversionFactor.apply(openCourse.time());
        return marginCounterCurrency.divide(conversionFactorValue).round(2);
    }
}
