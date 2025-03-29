package com.becker.freelance.tradeexecution.calculation;

import com.becker.freelance.commons.calculation.ProfitLossCalculation;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class TradingCalculatorImpl implements TradingCalculator {

    private TimeSeries eurUsd;

    public TradingCalculatorImpl(TimeSeries eurUsd) {
        this.eurUsd = eurUsd;
    }

    @Override
    public Decimal getProfitPerPoint(Pair pair, Decimal size) {
        return size.multiply(pair.profitPerPointForOneContract());
    }

    @Override
    public Decimal getDistanceByAmount(Pair pair, Decimal size, Decimal amount) {
        Decimal distance = amount.divide(size.multiply(pair.profitPerPointForOneContract()));
        return distance;
    }

    @Override
    public ProfitLossCalculation getProfitInEuro(Position position, Decimal currentPrice, LocalDateTime time) {
        Decimal diff = currentPrice.subtract(position.getOpenPrice());
        Decimal profitPerPoint = getProfitPerPoint(position.getPair(), position.getSize());
        Decimal profitCounterCurrency = diff.multiply(profitPerPoint).multiply(new Decimal(position.getDirection().getFactor()));
        Decimal conversionRate = position.getPair().isEuroCounterCurrency() ? Decimal.ONE : eurUsd.getEntryForTime(time).getCloseMid();
        Decimal profitEuro = profitCounterCurrency.divide(conversionRate).round(2);
        return new ProfitLossCalculation(conversionRate, profitEuro, currentPrice);
    }
}
