package com.becker.freelance.commons.position;


import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class HardLimitPosition extends Position {

    public static Position fromDistancesInEuros(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                                                Decimal stopInEuros, Decimal limitInEuros, Decimal margin){

        Decimal limitLevel = Position.getLimitLevelFromDistanceInEuro(tradingCalculator, direction, openPrice, limitInEuros, size, pair);
        Decimal stopLevel = Position.getStopLevelFromDistanceInEuro(tradingCalculator, direction, openPrice, stopInEuros, size, pair);

        return fromLevels(tradingCalculator, size, direction, openPrice, pair, stopLevel, limitLevel, margin);
    }

    public static Position fromLevels(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair, Decimal stopLevel, Decimal limitLevel, Decimal margin) {
        return new HardLimitPosition(tradingCalculator, size, direction, openPrice, pair, stopLevel, limitLevel, margin);
    }

    HardLimitPosition(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                      Decimal stopLevel, Decimal limitLevel, Decimal margin) {
        super(tradingCalculator, size, direction, openPrice, pair, stopLevel, limitLevel, PositionType.HARD_LIMIT, margin);
    }

    @Override
    public void adapt(TimeSeriesEntry currentPrice) {
        // No adaptation needed for hard limit position
    }
}
