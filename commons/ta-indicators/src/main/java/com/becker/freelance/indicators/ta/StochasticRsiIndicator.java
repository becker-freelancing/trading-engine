package com.becker.freelance.indicators.ta;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.StochasticOscillatorKIndicator;
import org.ta4j.core.num.Num;

public class StochasticRsiIndicator implements Indicator<RsiResult> {

    private final Indicator<Num> baseIndicator;
    private final RSIIndicator rsiIndicator;
    private final StochasticOscillatorKIndicator stochasticOscillatorKIndicator;
    private final SMAIndicator kIndicator;
    private final SMAIndicator dIndicator;

    public StochasticRsiIndicator(Indicator<Num> indicator, int rsiPeriod, int stochasticPeriod, int kSmoothing, int dSmoothing) {
        this.baseIndicator = indicator;
        this.rsiIndicator = new RSIIndicator(indicator, rsiPeriod);
        IndicatorHighPriceIndicator highPriceIndicator = new IndicatorHighPriceIndicator(rsiIndicator);
        IndicatorLowPriceIndicator lowPriceIndicator = new IndicatorLowPriceIndicator(rsiIndicator);
        this.stochasticOscillatorKIndicator = new StochasticOscillatorKIndicator(rsiIndicator, stochasticPeriod, highPriceIndicator, lowPriceIndicator);
        this.kIndicator = new SMAIndicator(stochasticOscillatorKIndicator, kSmoothing);
        this.dIndicator = new SMAIndicator(kIndicator, dSmoothing);
    }

    @Override
    public RsiResult getValue(int index) {
        Num kValue = kIndicator.getValue(index);
        Num dValue = dIndicator.getValue(index);
        return new RsiResult(kValue, dValue);
    }

    @Override
    public int getUnstableBars() {
        return rsiIndicator.getUnstableBars() + stochasticOscillatorKIndicator.getUnstableBars() + kIndicator.getUnstableBars() + dIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return baseIndicator.getBarSeries();
    }
}
