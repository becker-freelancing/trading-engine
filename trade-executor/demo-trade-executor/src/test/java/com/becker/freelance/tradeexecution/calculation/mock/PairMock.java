package com.becker.freelance.tradeexecution.calculation.mock;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.util.Objects;

public class PairMock implements Pair {

    public static Pair eurUsd(){
        return new PairMock("EUR", "USD", 100_000, 0.0333);
    }

    public static Pair ethEur(){
        return new PairMock("ETH", "EUR", 1, 0.5);
    }

    public static Pair gldUsd(){
        return new PairMock("GLD", "USD", 1, 0.05);
    }

    public static Pair xbtEur(){
       return new PairMock("XBT", "EUR", 1, 0.5);
    }

    private final String baseCurrency;
    private final String counterCurrency;
    private final Decimal sizeMultiplication;
    private final Decimal leverageFactor;

    public PairMock(String baseCurrency, String counterCurrency, long sizeMultiplication, double leverageFactor) {
        this.baseCurrency = baseCurrency;
        this.counterCurrency = counterCurrency;
        this.sizeMultiplication = new Decimal(sizeMultiplication);
        this.leverageFactor = new Decimal(leverageFactor);
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
    public Decimal sizeMultiplication() {
        return sizeMultiplication;
    }

    @Override
    public Decimal leverageFactor() {
        return leverageFactor;
    }

    @Override
    public Decimal profitPerPointForOneContract() {
        return Decimal.TEN.multiply(sizeMultiplication());
    }

    @Override
    public Decimal minOrderSize() {
        return Decimal.ZERO;
    }

    @Override
    public Decimal minStop() {
        return Decimal.ZERO;
    }

    @Override
    public Decimal minLimit() {
        return Decimal.ZERO;
    }

    @Override
    public String technicalName() {
        return "";
    }

    @Override
    public long timeInMinutes() {
        return 1;
    }

    @Override
    public boolean isExecutableInAppMode(AppMode appMode) {
        return false;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        PairMock pairMock = (PairMock) object;
        return Objects.equals(baseCurrency, pairMock.baseCurrency) && Objects.equals(counterCurrency, pairMock.counterCurrency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseCurrency, counterCurrency);
    }
}
