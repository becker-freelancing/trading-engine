package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.function.Function;

public class TradingCalculator {

    private TimeSeries umrechnungsKurs;
    private Function<LocalDateTime, Decimal> conversionRate;
    private final Pair pair;

    private Decimal noopUmrechnung(LocalDateTime time) {
        return Decimal.ONE;
    }

    private Decimal eurUsdUmrechnung(LocalDateTime time) {
        TimeSeriesEntry entry = umrechnungsKurs.getEntryForTime(time);
        return entry.getCloseMid();
    }

    public TradingCalculator(Pair pair, TimeSeries timeSeries) {
        if (!timeSeries.getPair().equals(Pair.eurUsd1())) {
            throw new IllegalArgumentException("No EUR/USD Time Series provided");
        }
        this.pair = pair;

        if ("USD".equals(pair.counterCurrency())) {
            this.umrechnungsKurs = timeSeries;
            this.conversionRate = this::eurUsdUmrechnung;
        } else if ("EUR".equals(pair.counterCurrency())) {
            this.conversionRate = this::noopUmrechnung;
        }
    }

    public ProfitLossResult calcProfitLoss(Decimal open, Decimal close, LocalDateTime closeTime, Direction direction, Decimal profitPerPoint) {
        Decimal diff = close.subtract(open);
        Decimal profitCounterCurrency = diff.multiply(profitPerPoint).multiply(new Decimal(direction.getFactor()));
        Decimal conversionRateFactor = conversionRate.apply(closeTime);
        return new ProfitLossResult(profitCounterCurrency.divide(conversionRateFactor).round(2), conversionRateFactor);
    }

    public Decimal calcDistanceInEurosFromDistanceInPointsAbsolute(Decimal distanceInEuros, Decimal size, LocalDateTime openTime, Decimal profitPerPoint){

        Decimal conversionRateFactor = conversionRate.apply(openTime);
        return distanceInEuros.multiply(conversionRateFactor).divide(profitPerPoint).round(8).abs();
    }

    public record ProfitLossResult(Decimal profit, Decimal conversionRate) {
    }
}
