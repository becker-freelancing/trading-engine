package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.util.VolatilityIndicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Optional;

public class MarketRegimeIndicator extends CachableIndicator<Integer, MarketRegime> implements Indicator<MarketRegime> {

    private final Num MINUS_1 = DecimalNum.valueOf(-1);

    private final double volaSplitThreshold;
    private final Num trendReversalSlopeThreshold;
    private final int trendSlopeShift;
    private final Indicator<Num> closePrice;
    private final Indicator<Num> ema50;
    private final Indicator<Num> ema100;
    private final Indicator<Optional<Num>> volaIndicator;

    public MarketRegimeIndicator(Indicator<Num> closePrice, double volaSplitThreshold, double trendReversalSlopeThreshold, int trendSlopeShift) {
        super(100);
        this.volaSplitThreshold = volaSplitThreshold;
        this.trendReversalSlopeThreshold = DecimalNum.valueOf(trendReversalSlopeThreshold);
        this.trendSlopeShift = trendSlopeShift;
        this.closePrice = closePrice;
        this.ema50 = new EMAIndicator(closePrice, 50);
        this.ema100 = new EMAIndicator(closePrice, 100);
        this.volaIndicator = new VolatilityIndicator(closePrice, 30);
    }

    @Override
    public MarketRegime getValue(int index) {
        Optional<MarketRegime> cache = findInCache(index);
        if (cache.isPresent()) {
            return cache.get();
        }
        Num ema50Value = ema50.getValue(index);
        Num ema100Value = ema100.getValue(index);
        Num ema50Slope = ema50Value.minus(ema50.getValue(index - trendSlopeShift)).dividedBy(DecimalNum.valueOf(trendSlopeShift));
        TrendDirection trendDirection = getTrendDirection(ema50Value, ema100Value, ema50Slope);
        Vola vola = getVola(index);

        MarketRegime marketRegime = map(trendDirection, vola);
        putInCache(index, marketRegime);
        return marketRegime;
    }

    private MarketRegime map(TrendDirection trendDirection, Vola vola) {
        return switch (trendDirection) {
            case UP -> switch (vola) {
                case HIGH -> MarketRegime.UP_HIGH_VOLA;
                case LOW -> MarketRegime.UP_LOW_VOLA;
            };
            case DOWN -> switch (vola) {
                case HIGH -> MarketRegime.DOWN_HIGH_VOLA;
                case LOW -> MarketRegime.DOWN_LOW_VOLA;
            };
            case SIDE -> switch (vola) {
                case HIGH -> MarketRegime.SIDE_HIGH_VOLA;
                case LOW -> MarketRegime.SIDE_LOW_VOLA;
            };
        };
    }

    private Vola getVola(int index) {
        Double vola = volaIndicator.getValue(index).map(Num::doubleValue).orElse(0.);

        if (vola <= volaSplitThreshold) {
            return Vola.LOW;
        }
        return Vola.HIGH;
    }

    private TrendDirection getTrendDirection(Num ema50, Num ema100, Num ema50Slope) {
        if (ema50.isGreaterThan(ema100) && ema50Slope.isGreaterThan(trendReversalSlopeThreshold.multipliedBy(MINUS_1))) {
            return TrendDirection.UP;
        }
        if (ema50.isLessThan(ema100) && ema50Slope.isLessThan(trendReversalSlopeThreshold)) {
            return TrendDirection.DOWN;
        }

        return TrendDirection.SIDE;
    }

    @Override
    public int getUnstableBars() {
        return ema100.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return ema50.getBarSeries();
    }

    private static enum TrendDirection {
        UP,
        DOWN,
        SIDE
    }

    private static enum Vola {
        HIGH,
        LOW
    }
}
