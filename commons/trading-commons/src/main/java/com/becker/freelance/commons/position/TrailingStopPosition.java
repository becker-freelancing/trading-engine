package com.becker.freelance.commons.position;


import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class TrailingStopPosition extends Position {

    public static Position fromDistancesInEuro(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                                               Decimal stopInPointsInEuros, Decimal limitInEuros, Decimal trailingStepSizeInEuro, Decimal margin) {

        Decimal limitLevel = Position.getLimitLevelFromDistanceInEuro(tradingCalculator, direction, openPrice, limitInEuros, size, pair);
        Decimal stopLevel = Position.getStopLevelFromDistanceInEuro(tradingCalculator, direction, openPrice, stopInPointsInEuros, size, pair);

        return fromLevels(tradingCalculator, size, direction, openPrice, pair, stopLevel, limitLevel, trailingStepSizeInEuro, margin);
    }

    public static Position fromLevels(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair, Decimal stopLevel, Decimal limitLevel, Decimal trailingStepSizeInEuro, Decimal margin) {
        return new TrailingStopPosition(tradingCalculator, size, direction, openPrice, pair, stopLevel, limitLevel, trailingStepSizeInEuro, margin);
    }

    private Decimal trailingStepSizeInEuro;
    private Decimal nextTrailingUpdateProfit;
    private Decimal lastTrailingUpdateValue;

    TrailingStopPosition(TradingCalculator tradingCalculator, Decimal size, Direction direction, TimeSeriesEntry openPrice, Pair pair,
                         Decimal stopLevel, Decimal limitLevel, Decimal trailingStepSizeInEuro, Decimal margin) {
        super(tradingCalculator, size, direction, openPrice, pair, stopLevel, limitLevel, PositionType.TRAILING, margin);
        this.trailingStepSizeInEuro = trailingStepSizeInEuro;
        this.nextTrailingUpdateProfit = trailingStepSizeInEuro;
        this.lastTrailingUpdateValue = openPrice.getCloseMid();
    }

    @Override
    public void adapt(TimeSeriesEntry currentPrice) {
        if (direction == Direction.BUY){
            updateForBuyPosition(currentPrice);
        } else if (direction == Direction.SELL) {
            updateForSellPosition(currentPrice);
        } else {
            throw new IllegalArgumentException("No Trailing implemented for Direction " + direction);
        }
    }

    private void updateForBuyPosition(TimeSeriesEntry currentPrice) {
        if (lastTrailingUpdateValue.isGreaterThanOrEqualTo(currentPrice.getCloseMid())){
            return;
        }

        Decimal profit = currentProfit(currentPrice).profit();
        if (profit.isLessThan(nextTrailingUpdateProfit)){
            return;
        }


        nextTrailingUpdateProfit = profit.add(trailingStepSizeInEuro);
        stopLevel = stopLevel.add(currentPrice.getCloseMid().subtract(lastTrailingUpdateValue).abs());
        lastTrailingUpdateValue = currentPrice.getCloseMid();
    }


    private void updateForSellPosition(TimeSeriesEntry currentPrice) {
        if (lastTrailingUpdateValue.isLessThanOrEqualTo(currentPrice.getCloseMid())){
            return;
        }

        Decimal profit = currentProfit(currentPrice).profit();
        if (profit.isLessThan(nextTrailingUpdateProfit)){
            return;
        }


        nextTrailingUpdateProfit = profit.add(trailingStepSizeInEuro);
        stopLevel = stopLevel.subtract(currentPrice.getCloseMid().subtract(lastTrailingUpdateValue).abs());
        lastTrailingUpdateValue = currentPrice.getCloseMid();
    }

    public Decimal getTrailingStepSizeInEuro() {
        return trailingStepSizeInEuro;
    }
}
