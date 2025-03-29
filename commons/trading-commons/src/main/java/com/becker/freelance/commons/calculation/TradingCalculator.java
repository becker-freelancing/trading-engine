package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface TradingCalculator {

    public Decimal getProfitPerPoint(Pair pair, Decimal size);

    public Decimal getDistanceByAmount(Pair pair, Decimal size, Decimal amount);

    public ProfitLossCalculation getProfitInEuro(Position position, Decimal currentPrice, LocalDateTime time);

    public default ProfitLossCalculation getProfitInEuro(Position position, TimeSeriesEntry currentPrice, LocalDateTime time) {
        return switch (position.getDirection()) {
            case SELL -> getProfitInEuro(position, currentPrice.closeAsk(), time);
            case BUY -> getProfitInEuro(position, currentPrice.closeBid(), time);
        };
    }
}
