package com.becker.freelance.commons;

import com.becker.freelance.commons.pair.Pair;

import java.time.LocalDateTime;

public class Trade {

    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Pair pair;
    private double profitInEuro;
    private double openLevel;
    private double closeLevel;
    private double size;
    private Direction direction;
    private double conversionRate;
    private PositionType positionType;

    protected Trade(){}

    public Trade(LocalDateTime openTime, LocalDateTime closeTime, Pair pair, double profitInEuro,
                 double openLevel, double closeLevel, double size, Direction direction,
                 double conversionRate, PositionType positionType) {
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.pair = pair;
        this.profitInEuro = profitInEuro;
        this.openLevel = openLevel;
        this.closeLevel = closeLevel;
        this.size = size;
        this.direction = direction;
        this.conversionRate = conversionRate;
        this.positionType = positionType;
    }

    @Override
    public String toString() {
        return String.format("Trade(openTime=%s, closeTime=%s, pair=%s, profitInEuro=%.2f, openLevel=%.4f, " +
                        "closeLevel=%.4f, size=%.2f, direction=%s, conversionRate=%.4f, positionType=%s)",
                openTime, closeTime, pair, profitInEuro, openLevel, closeLevel, size,
                direction, conversionRate, positionType);
    }

    public LocalDateTime getOpenTime() {
        return openTime;
    }

    public LocalDateTime getCloseTime() {
        return closeTime;
    }

    public Pair getPair() {
        return pair;
    }

    public double getProfitInEuro() {
        return profitInEuro;
    }

    public double getOpenLevel() {
        return openLevel;
    }

    public double getCloseLevel() {
        return closeLevel;
    }

    public double getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public PositionType getPositionType() {
        return positionType;
    }
}
