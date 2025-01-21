package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.Pair;
import com.becker.freelance.commons.TimeSeries;
import com.becker.freelance.commons.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.function.Function;

public class MarginCalculator {

    private TimeSeries conversionRate;
    private Function<LocalDateTime, Double> conversionFactor;
    private double leverageFactor;
    private Pair pair;

    private double noopConversion(LocalDateTime time) {
        return 1.0;
    }

    private double eurusdConversion(LocalDateTime time) {
        TimeSeriesEntry entry = conversionRate.getEntryForTime(time);
        double mid = (entry.getCloseAsk() + entry.getCloseBid()) / 2.0;
        return mid;
    }

    public MarginCalculator(Pair pair, TimeSeries timeSeries) {
        if (!timeSeries.getPair().equals(Pair.EURUSD_1)) {
            throw new IllegalArgumentException("No EUR/USD Time Series provided");
        }
        this.pair = pair;
        this.leverageFactor = pair.getLeverageFactor();

        if ("USD".equals(pair.getCounterCurrency())) {
            this.conversionRate = timeSeries;
            this.conversionFactor = this::eurusdConversion;
        } else if ("EUR".equals(pair.getCounterCurrency())) {
            this.conversionFactor = this::noopConversion;
        }
    }

    public double calcMargin(double size, TimeSeriesEntry openCourse) {
        double marginCounterCurrency = size * leverageFactor * openCourse.closeMid() * pair.getSizeMultiplication();
        double conversionFactorValue = conversionFactor.apply(openCourse.getTime());
        return Math.round(marginCounterCurrency / conversionFactorValue * 100.0) / 100.0;
    }
}
