package com.becker.freelance.indicators.ta.trend;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.swing.SwingHighIndicator;
import com.becker.freelance.indicators.ta.swing.SwingHighPoint;
import com.becker.freelance.indicators.ta.swing.SwingLowIndicator;
import com.becker.freelance.indicators.ta.swing.SwingLowPoint;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.Num;

import java.util.List;
import java.util.Optional;

public class TrendIndicator extends CachableIndicator<Integer, Trend> implements Indicator<Trend> {

    private final SwingHighIndicator swingHighIndicator;
    private final SwingLowIndicator swingLowIndicator;
    private final int numberForValidation;
    private final Num courseErrorRatio;

    public TrendIndicator(BarSeries barSeries, int period, int numberForValidation, Num courseErrorRatio) {
        super(1000);
        this.numberForValidation = numberForValidation;
        this.courseErrorRatio = courseErrorRatio;
        this.swingHighIndicator = new SwingHighIndicator(period, new HighPriceIndicator(barSeries), period + 100);
        this.swingLowIndicator = new SwingLowIndicator(period, new LowPriceIndicator(barSeries), period + 100);
    }

    @Override
    public Trend getValue(int index) {
        Optional<Trend> inCache = findInCache(index);

        if (inCache.isPresent()) {
            return inCache.get();
        }

        List<SwingLowPoint> lastNSwingLows = swingLowIndicator.getLastNStableSwings(index, numberForValidation);
        List<SwingHighPoint> lastNSwingHighs = swingHighIndicator.getLastNStableSwings(index, numberForValidation);

        if (lastNSwingLows.size() < numberForValidation || lastNSwingHighs.size() < numberForValidation) {
            return new Trend(TrendDirection.UNDEFINED);
        }
        Trend result = new Trend(TrendDirection.SIDE, lastNSwingHighs, lastNSwingLows);

        if (isUpTrend(lastNSwingHighs, lastNSwingLows)) {
            result = new Trend(TrendDirection.UP, lastNSwingHighs, lastNSwingLows);
        } else if (isDownTrend(lastNSwingHighs, lastNSwingLows)) {
            result = new Trend(TrendDirection.DOWN, lastNSwingHighs, lastNSwingLows);
        }

        putInCache(index, result);
        return result;
    }

    private boolean isUpTrend(List<SwingHighPoint> lastNSwingHighs, List<SwingLowPoint> lastNSwingLows) {
        Num lastHigh = lastNSwingHighs.get(0).candleValue();

        for (SwingHighPoint lastNSwingHigh : lastNSwingHighs) {
            Num currentHighValue = lastNSwingHigh.candleValue();
            if (isNotHigherOrInLowerRange(lastHigh, currentHighValue)) {
                return false;
            }
            lastHigh = currentHighValue;
        }

        Num lastLow = lastNSwingLows.get(0).candleValue();

        for (SwingLowPoint lastNSwingLow : lastNSwingLows) {
            Num currentLowValue = lastNSwingLow.candleValue();
            if (isNotHigherOrInLowerRange(lastLow, currentLowValue)) {
                return false;
            }
            lastLow = currentLowValue;
        }

        return true;
    }

    private boolean isNotHigherOrInLowerRange(Num baseValue, Num testValue) {
        Num minValue = baseValue.minus(baseValue.multipliedBy(courseErrorRatio));
        return !testValue.isGreaterThan(minValue);
    }

    private boolean isNotLowerOrInHigherRange(Num baseValue, Num testValue) {
        Num minValue = baseValue.plus(baseValue.multipliedBy(courseErrorRatio));
        return !testValue.isLessThan(minValue);
    }


    private boolean isDownTrend(List<SwingHighPoint> lastNSwingHighs, List<SwingLowPoint> lastNSwingLows) {
        Num lastHigh = lastNSwingHighs.get(0).candleValue();

        for (SwingHighPoint lastNSwingHigh : lastNSwingHighs) {
            Num currentHighValue = lastNSwingHigh.candleValue();
            if (isNotLowerOrInHigherRange(lastHigh, currentHighValue)) {
                return false;
            }
            lastHigh = currentHighValue;
        }

        Num lastLow = lastNSwingLows.get(0).candleValue();

        for (SwingLowPoint lastNSwingLow : lastNSwingLows) {
            Num currentLowValue = lastNSwingLow.candleValue();
            if (isNotLowerOrInHigherRange(lastLow, currentLowValue)) {
                return false;
            }
            lastLow = currentLowValue;
        }

        return true;
    }

    @Override
    public int getUnstableBars() {
        return swingHighIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return swingHighIndicator.getBarSeries();
    }
}
