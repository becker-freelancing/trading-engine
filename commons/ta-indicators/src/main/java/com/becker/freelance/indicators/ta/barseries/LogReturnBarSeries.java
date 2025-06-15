package com.becker.freelance.indicators.ta.barseries;

import com.becker.freelance.indicators.ta.util.LogReturnIndicator;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.helpers.OpenPriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.IntStream;

public class LogReturnBarSeries implements BarSeries {

    private final BarSeries baseSeries;
    private final LogReturnIndicator open;
    private final LogReturnIndicator high;
    private final LogReturnIndicator low;
    private final LogReturnIndicator close;

    public LogReturnBarSeries(BarSeries baseSeries) {
        this.baseSeries = baseSeries;
        this.open = new LogReturnIndicator(new OpenPriceIndicator(baseSeries));
        this.high = new LogReturnIndicator(new HighPriceIndicator(baseSeries));
        this.low = new LogReturnIndicator(new LowPriceIndicator(baseSeries));
        this.close = new LogReturnIndicator(new ClosePriceIndicator(baseSeries));
    }

    @Override
    public String getName() {
        return baseSeries.getName() + "_logReturn";
    }

    @Override
    public Num num() {
        return baseSeries.num();
    }

    @Override
    public Bar getBar(int i) {
        Bar bar = baseSeries.getBar(i);
        if (i == 0) {
            return new BaseBar(
                    bar.getTimePeriod(),
                    bar.getEndTime(),
                    DecimalNum.ZERO,
                    DecimalNum.ZERO,
                    DecimalNum.ZERO,
                    DecimalNum.ZERO,
                    bar.getVolume(),
                    bar.getAmount()
            );

        }
        return new BaseBar(
                bar.getTimePeriod(),
                bar.getEndTime(),
                open.getValue(i),
                high.getValue(i),
                low.getValue(i),
                close.getValue(i),
                bar.getVolume(),
                bar.getAmount()
        );
    }

    @Override
    public int getBarCount() {
        return baseSeries.getBarCount();
    }

    @Override
    public List<Bar> getBarData() {
        return IntStream.rangeClosed(getBeginIndex(), getEndIndex())
                .mapToObj(this::getBar)
                .toList();
    }

    @Override
    public int getBeginIndex() {
        return baseSeries.getBeginIndex();
    }

    @Override
    public int getEndIndex() {
        return baseSeries.getEndIndex();
    }

    @Override
    public int getMaximumBarCount() {
        return baseSeries.getMaximumBarCount();
    }

    @Override
    public void setMaximumBarCount(int maximumBarCount) {
        baseSeries.setMaximumBarCount(maximumBarCount);
    }

    @Override
    public int getRemovedBarsCount() {
        return baseSeries.getRemovedBarsCount();
    }

    @Override
    public void addBar(Bar bar, boolean replace) {
        baseSeries.addBar(bar, replace);
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime) {
        baseSeries.addBar(timePeriod, endTime);
    }

    @Override
    public void addBar(ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume, Num amount) {
        baseSeries.addBar(endTime, openPrice, highPrice, lowPrice, closePrice, volume, amount);
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume) {

        baseSeries.addBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume);
    }

    @Override
    public void addBar(Duration timePeriod, ZonedDateTime endTime, Num openPrice, Num highPrice, Num lowPrice, Num closePrice, Num volume, Num amount) {

        baseSeries.addBar(timePeriod, endTime, openPrice, highPrice, lowPrice, closePrice, volume, amount);
    }

    @Override
    public void addTrade(Num tradeVolume, Num tradePrice) {
        baseSeries.addTrade(tradeVolume, tradePrice);
    }

    @Override
    public void addPrice(Num price) {
        baseSeries.addPrice(price);
    }

    @Override
    public BarSeries getSubSeries(int startIndex, int endIndex) {
        return new LogReturnBarSeries(baseSeries.getSubSeries(startIndex, endIndex));
    }
}
