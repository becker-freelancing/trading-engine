package com.becker.freelance.commons.trade;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public class Trade implements Comparable<Trade> {

    private String relatedPositionId;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    private Pair pair;
    private Decimal profitInEuroWithFees;
    private Decimal openLevel;
    private Decimal closeLevel;
    private Decimal openFee;
    private Decimal closeFee;
    private boolean forceClosed;

    private Decimal totalFee;
    private Decimal size;
    private Direction direction;
    private Decimal conversionRate;
    private PositionBehaviour positionBehaviour;
    private TradeableMarketRegime openMarketRegime;

    protected Trade(){}

    public Trade(String relatedPositionId,
                 LocalDateTime openTime,
                 LocalDateTime closeTime,
                 Pair pair,
                 Decimal profitInEuroWithFees,
                 Decimal openLevel,
                 Decimal closeLevel,
                 Decimal openFee,
                 Decimal closeFee,
                 Decimal totalFee,
                 Decimal size,
                 Direction direction,
                 Decimal conversionRate,
                 PositionBehaviour positionBehaviour,
                 TradeableMarketRegime openMarketRegime,
                 boolean forceClosed) {
        this.relatedPositionId = relatedPositionId;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.pair = pair;
        this.profitInEuroWithFees = profitInEuroWithFees;
        this.openLevel = openLevel;
        this.closeLevel = closeLevel;
        this.totalFee = totalFee;
        this.size = size;
        this.direction = direction;
        this.conversionRate = conversionRate;
        this.positionBehaviour = positionBehaviour;
        this.openMarketRegime = openMarketRegime;
        this.openFee = openFee;
        this.closeFee = closeFee;
        this.forceClosed = forceClosed;
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

    public Decimal getProfitInEuroWithFees() {
        return profitInEuroWithFees;
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

    public boolean isForceClosed() {
        return forceClosed;
    }

    public Decimal getConversionRate() {
        return conversionRate;
    }

    public PositionBehaviour getPositionBehaviour() {
        return positionBehaviour;
    }

    public TradeableMarketRegime getOpenMarketRegime() {
        return openMarketRegime;
    }

    public Decimal getOpenFee() {
        return openFee;
    }

    public Decimal getCloseFee() {
        return closeFee;
    }

    public String getRelatedPositionId() {
        return relatedPositionId;
    }

    public Decimal getTotalFee() {
        return totalFee;
    }

    @Override
    public int compareTo(Trade o) {
        return this.closeTime.compareTo(o.closeTime);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "openTime=" + openTime +
                ", closeTime=" + closeTime +
                ", pair=" + pair +
                ", profitInEuroWithFees=" + profitInEuroWithFees +
                ", openLevel=" + openLevel +
                ", closeLevel=" + closeLevel +
                ", openFee=" + openFee +
                ", closeFee=" + closeFee +
                ", size=" + size +
                ", direction=" + direction +
                ", conversionRate=" + conversionRate +
                ", positionType=" + positionBehaviour +
                ", openMarketRegime=" + openMarketRegime +
                '}';
    }
}
