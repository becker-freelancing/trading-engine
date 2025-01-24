package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.Direction;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.TimeSeries;
import com.becker.freelance.commons.TimeSeriesEntry;

import java.time.LocalDateTime;
import java.util.function.Function;

public class TradingCalculator {

    private TimeSeries umrechnungsKurs;
    private Function<LocalDateTime, Double> umrechnungsFactor;
    private Pair pair;

    private double noopUmrechnung(LocalDateTime time) {
        return 1.0;
    }

    private double eurUsdUmrechnung(LocalDateTime time) {
        TimeSeriesEntry entry = umrechnungsKurs.getEntryForTime(time);
        double mid = (entry.getCloseAsk() + entry.getCloseBid()) / 2.0;
        return mid;
    }

    public TradingCalculator(Pair pair, TimeSeries timeSeries) {
        if (!timeSeries.getPair().equals(Pair.eurUsd1())) {
            throw new IllegalArgumentException("No EUR/USD Time Series provided");
        }
        this.pair = pair;

        if ("USD".equals(pair.counterCurrency())) {
            this.umrechnungsKurs = timeSeries;
            this.umrechnungsFactor = this::eurUsdUmrechnung;
        } else if ("EUR".equals(pair.counterCurrency())) {
            this.umrechnungsFactor = this::noopUmrechnung;
        }
    }

    public ProfitLossResult calcProfitLoss(double open, double close, LocalDateTime closeTime, Direction direction, double profitPerPoint) {
        double diff = close - open;
        double profitGegenwaehrung = diff * profitPerPoint * direction.getFactor() * pair.sizeMultiplication();
        double umrechnungsFactorValue = umrechnungsFactor.apply(closeTime);
        return new ProfitLossResult(Math.round(profitGegenwaehrung / umrechnungsFactorValue * 100.0) / 100.0, umrechnungsFactorValue);
    }

        public record ProfitLossResult(double profit, double umrechnungsFactor) {
    }
}
