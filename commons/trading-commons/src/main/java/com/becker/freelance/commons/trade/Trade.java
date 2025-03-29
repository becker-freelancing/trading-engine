package com.becker.freelance.commons.trade;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class Trade {

    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Pair pair;
    private Decimal profitInEuro;
    private Decimal openLevel;
    private Decimal closeLevel;
    private Decimal size;
    private Direction direction;
    private Decimal conversionRate;
    private PositionType positionType;

    protected Trade(){}

    public Trade(LocalDateTime openTime, LocalDateTime closeTime, Pair pair, Decimal profitInEuro,
                 Decimal openLevel, Decimal closeLevel, Decimal size, Direction direction,
                 Decimal conversionRate, PositionType positionType) {
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

    public Decimal getProfitInEuro() {
        return profitInEuro;
    }

    public Decimal getOpenLevel() {
        return openLevel;
    }

    public Decimal getCloseLevel() {
        return closeLevel;
    }

    public Decimal getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public Decimal getConversionRate() {
        return conversionRate;
    }

    public PositionType getPositionType() {
        return positionType;
    }

}
