package com.becker.freelance.commons.timeseries;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public record TimeSeriesEntry(LocalDateTime time, Decimal openBid, Decimal openAsk, Decimal highBid, Decimal highAsk,
                              Decimal lowBid, Decimal lowAsk, Decimal closeBid, Decimal closeAsk, Decimal volume,
                              Decimal trades, Pair pair) {

    public Decimal getCloseMid() {
        return closeAsk().add(closeBid()).divide(Decimal.TWO);
    }

    public Decimal getOpenMid() {
        return openAsk().add(openBid()).divide(Decimal.TWO);
    }

    public Decimal getHighMid() {
        return highAsk().add(highBid()).divide(Decimal.TWO);
    }

    public Decimal getLowMid() {
        return lowAsk().add(lowBid()).divide(Decimal.TWO);
    }

    public Decimal getCloseSpread() {
        return closeBid().subtract(closeAsk());
    }

    public Decimal getClosePriceForDirection(Direction direction) {
        return switch (direction) {
            case BUY -> closeAsk();
            case SELL -> closeBid();
        };
    }

    public Decimal getLowPriceForDirection(Direction direction) {
        return switch (direction) {
            case BUY -> lowAsk();
            case SELL -> lowBid();
        };
    }

    public Decimal getHighPriceForDirection(Direction direction) {
        return switch (direction) {
            case BUY -> highAsk();
            case SELL -> highBid();
        };
    }

    public Decimal geOpenPriceForDirection(Direction direction) {
        return switch (direction) {
            case BUY -> openAsk();
            case SELL -> openBid();
        };
    }

    @Override
    public String toString() {
        return String.format("TimeSeriesEntry(Time: %s, Pair: %s, Open: %f, High: %f, Low: %f, Close: %f, Volume: %f, Trades: %f)",
                time, pair.technicalName(), openAsk, highAsk, lowAsk, closeAsk, volume, trades);
    }
}
