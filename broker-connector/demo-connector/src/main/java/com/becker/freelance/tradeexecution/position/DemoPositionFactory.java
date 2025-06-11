package com.becker.freelance.tradeexecution.position;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.position.PositionFactory;
import com.becker.freelance.commons.position.StopLimitPosition;
import com.becker.freelance.commons.position.TrailingPosition;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

public class DemoPositionFactory implements PositionFactory {

    private final EurUsdRequestor eurUsd;
    private final TradingFeeCalculator tradingFeeCalculator;
    private final MarginCalculator marginCalculator;

    public DemoPositionFactory(EurUsdRequestor eurUsd, TradingFeeCalculator tradingFeeCalculator, MarginCalculator marginCalculator) {
        this.eurUsd = eurUsd;
        this.tradingFeeCalculator = tradingFeeCalculator;
        this.marginCalculator = marginCalculator;
    }


    @Override
    public StopLimitPosition createStopLimitPosition(EntrySignal entrySignal) {
        return new DemoStopLimitPosition(
                entrySignal.openMarketRegime(),
                tradingFeeCalculator,
                marginCalculator,
                entrySignal.getLimitOrder(),
                entrySignal.getStopOrder(),
                entrySignal.getOpenOrder()
        );
    }

    @Override
    public TrailingPosition createTrailingPosition(EntrySignal entrySignal, TimeSeriesEntry currentPrice) {
        return new DemoTrailingPosition(
                entrySignal.getStopOrder().getEstimatedExecutionLevel(currentPrice),
                entrySignal.openMarketRegime(),
                tradingFeeCalculator,
                marginCalculator,
                entrySignal.getLimitOrder(),
                entrySignal.getStopOrder(),
                entrySignal.getOpenOrder()
        );
    }


}
