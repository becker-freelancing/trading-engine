package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;

import java.time.LocalDateTime;

public record TimeSeriesEntry(LocalDateTime time, double openBid, double openAsk, double highBid, double highAsk,
                              double lowBid, double lowAsk, double closeBid, double closeAsk, double volume,
                              double trades, Pair pair) {

    public double getCloseMid() {
        return (closeAsk + closeBid) / 2;
    }

    public double getOpenMid() {
        return (openAsk + openBid) / 2;
    }

    public double getHighMid() {
        return (highAsk + highBid) / 2;
    }

    public double getLowMid() {
        return (lowAsk + lowBid) / 2;
    }

    @Override
    public String toString() {
        return String.format("TimeSeriesEntry(Time: %s, Open: %f, High: %f, Low: %f, Close: %f, Volume: %f, Trades: %f)",
                time, openAsk, highAsk, lowAsk, closeAsk, volume, trades);
    }

    public boolean isGreenCandle() {
        return getCloseMid() > getOpenMid();
    }
}
