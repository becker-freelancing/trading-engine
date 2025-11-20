package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.indicators.ta.cache.CachableIndicator;
import com.becker.freelance.indicators.ta.other.EMATemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.TemporalBarSeries;
import com.becker.freelance.indicators.ta.temporal.TemporalIndicator;
import com.becker.freelance.indicators.ta.util.VolatilityIndicator;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public class MarketRegimeIndicator extends CachableIndicator<LocalDateTime, MarketRegime> implements TemporalIndicator<TradeableMarketRegime> {

    private final Decimal MINUS_1 = Decimal.MINUS_1;

    private final double volaSplitThreshold;
    private final Decimal trendReversalSlopeThreshold;
    private final int trendSlopeShift;
    private final TemporalIndicator<Decimal> ema50;
    private final TemporalIndicator<Decimal> ema100;
    private final TemporalIndicator<Optional<Double>> volaIndicator;

    public MarketRegimeIndicator(TemporalIndicator<Decimal> closePrice, double volaSplitThreshold, double trendReversalSlopeThreshold, int trendSlopeShift) {
        super(100);
        this.volaSplitThreshold = volaSplitThreshold;
        this.trendReversalSlopeThreshold = Decimal.valueOf(trendReversalSlopeThreshold);
        this.trendSlopeShift = trendSlopeShift;
        this.ema50 = new EMATemporalIndicator(closePrice, 50, false, false);
        this.ema100 = new EMATemporalIndicator(closePrice, 100, false, false);
        this.volaIndicator = new VolatilityIndicator(closePrice, 30);
    }

    @Override
    public MarketRegime getValue(LocalDateTime index) {
        return getOrCompute(index);
    }

    @Override
    protected MarketRegime computeMissing(LocalDateTime index) {
        Decimal ema50Value = ema50.getValue(index);
        Decimal ema100Value = ema100.getValue(index);
        Decimal ema50Slope = ema50Value.subtract(ema50.getValue(index.minus(getBarSeries().getPairDuration().multipliedBy(trendSlopeShift)))).divide(Decimal.valueOf(trendSlopeShift));
        TrendDirection trendDirection = getTrendDirection(ema50Value, ema100Value, ema50Slope);
        Vola vola = getVola(index);

        MarketRegime marketRegime = map(trendDirection, vola);
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

    private Vola getVola(LocalDateTime index) {
        Double vola = volaIndicator.getValue(index).orElse(0.);

        if (vola <= volaSplitThreshold) {
            return Vola.LOW;
        }
        return Vola.HIGH;
    }

    private TrendDirection getTrendDirection(Decimal ema50, Decimal ema100, Decimal ema50Slope) {
        if (ema50.isGreaterThan(ema100) && ema50Slope.isGreaterThan(trendReversalSlopeThreshold.multiply(MINUS_1))) {
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
    public TemporalBarSeries getBarSeries() {
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
