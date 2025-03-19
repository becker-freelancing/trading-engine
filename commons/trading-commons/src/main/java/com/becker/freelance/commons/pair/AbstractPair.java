package com.becker.freelance.commons.pair;

import com.becker.freelance.math.Decimal;

import java.util.Objects;

public abstract class AbstractPair implements Pair {
    private final String baseCurrency;
    private final String counterCurrency;
    private final long timeInMinutes;
    private final String technicalName;
    private final Decimal profitPerPointForOneContract;
    private final Decimal minOrderSize;
    private final Decimal minStop;
    private final Decimal minLimit;
    private final Decimal leverageFactor;
    private final Decimal sizeMultiplication;

    public AbstractPair(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Decimal profitPerPointForOneContract,
                        Decimal minOrderSize, Decimal minStop, Decimal minLimit, Decimal leverageFactor, Decimal sizeMultiplication) {
        this.baseCurrency = baseCurrency;
        this.counterCurrency = counterCurrency;
        this.timeInMinutes = timeInMinutes;
        this.technicalName = technicalName;
        this.profitPerPointForOneContract = profitPerPointForOneContract;
        this.minOrderSize = minOrderSize;
        this.minStop = minStop;
        this.minLimit = minLimit;
        this.leverageFactor = leverageFactor;
        this.sizeMultiplication = sizeMultiplication;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AbstractPair that = (AbstractPair) object;
        return timeInMinutes == that.timeInMinutes && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(technicalName, that.technicalName) && Objects.equals(counterCurrency, that.counterCurrency) && Objects.equals(minStop, that.minStop) && Objects.equals(minLimit, that.minLimit) && Objects.equals(minOrderSize, that.minOrderSize) && Objects.equals(leverageFactor, that.leverageFactor) && Objects.equals(sizeMultiplication, that.sizeMultiplication) && Objects.equals(profitPerPointForOneContract, that.profitPerPointForOneContract);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, counterCurrency, timeInMinutes, technicalName, profitPerPointForOneContract, minOrderSize, minStop, minLimit, leverageFactor, sizeMultiplication);
    }

    @Override
    public String baseCurrency() {
        return baseCurrency;
    }

    @Override
    public String counterCurrency() {
        return counterCurrency;
    }

    @Override
    public long timeInMinutes() {
        return timeInMinutes;
    }

    @Override
    public String technicalName() {
        return technicalName;
    }

    @Override
    public Decimal profitPerPointForOneContract() {
        return profitPerPointForOneContract;
    }

    @Override
    public Decimal minOrderSize() {
        return minOrderSize;
    }

    @Override
    public Decimal minStop() {
        return minStop;
    }

    @Override
    public Decimal minLimit() {
        return minLimit;
    }

    @Override
    public Decimal leverageFactor() {
        return leverageFactor;
    }

    @Override
    public Decimal sizeMultiplication() {
        return sizeMultiplication;
    }

    @Override
    public String toString() {
        return "AbstractPair[" +
                "baseCurrency=" + baseCurrency + ", " +
                "counterCurrency=" + counterCurrency + ", " +
                "timeInMinutes=" + timeInMinutes + ", " +
                "technicalName=" + technicalName + ", " +
                "profitPerPointForOneContract=" + profitPerPointForOneContract + ", " +
                "minOrderSize=" + minOrderSize + ", " +
                "minStop=" + minStop + ", " +
                "minLimit=" + minLimit + ", " +
                "leverageFactor=" + leverageFactor + ", " +
                "sizeMultiplication=" + sizeMultiplication + ']';
    }


}
