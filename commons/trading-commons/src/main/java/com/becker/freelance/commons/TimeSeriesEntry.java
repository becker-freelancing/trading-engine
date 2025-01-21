package com.becker.freelance.commons;

import java.time.LocalDateTime;

public class TimeSeriesEntry {
    private LocalDateTime time;
    private double openBid, openAsk, highBid, highAsk, lowBid, lowAsk, closeBid, closeAsk, volume, trades;
    private Pair pair;

    public TimeSeriesEntry(LocalDateTime time, double openBid, double openAsk, double highBid, double highAsk,
                           double lowBid, double lowAsk, double closeBid, double closeAsk,
                           double volume, double trades, Pair pair) {
        this.time = time;
        this.openBid = openBid;
        this.openAsk = openAsk;
        this.highBid = highBid;
        this.highAsk = highAsk;
        this.lowBid = lowBid;
        this.lowAsk = lowAsk;
        this.closeBid = closeBid;
        this.closeAsk = closeAsk;
        this.volume = volume;
        this.trades = trades;
        this.pair = pair;
    }

    public double closeMid() {
        return (closeAsk + closeBid) / 2;
    }
    public double openMid() {
        return (openAsk + openBid) / 2;
    }
    public double highMid() {
        return (highAsk + highBid) / 2;
    }
    public double lowMid() {
        return (lowAsk + lowBid) / 2;
    }

    @Override
    public String toString() {
        return String.format("TimeSeriesEntry(Time: %s, Open: %f, High: %f, Low: %f, Close: %f, Volume: %f, Trades: %f)",
                time, openAsk, highAsk, lowAsk, closeAsk, volume, trades);
    }

    public double getCloseAsk() {
        return closeAsk;
    }

    public double getCloseBid() {
        return closeBid;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public double getOpenBid() {
        return openBid;
    }

    public double getOpenAsk() {
        return openAsk;
    }

    public double getHighBid() {
        return highBid;
    }

    public double getHighAsk() {
        return highAsk;
    }

    public double getLowBid() {
        return lowBid;
    }

    public double getLowAsk() {
        return lowAsk;
    }

    public double getVolume() {
        return volume;
    }

    public double getTrades() {
        return trades;
    }

    public boolean isGreenCandle(){
        return closeMid() > openMid();
    }

    public Pair getPair() {
        return pair;
    }
}
