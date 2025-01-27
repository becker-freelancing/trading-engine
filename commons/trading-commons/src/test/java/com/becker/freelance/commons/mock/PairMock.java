package com.becker.freelance.commons.mock;

import com.becker.freelance.commons.pair.Pair;

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

    private String baseCurrency;
    private String counterCurrency;
    private double sizeMultiplication;
    private double leverageFactor;

    public PairMock(String baseCurrency, String counterCurrency, double sizeMultiplication, double leverageFactor) {
        this.baseCurrency = baseCurrency;
        this.counterCurrency = counterCurrency;
        this.sizeMultiplication = sizeMultiplication;
        this.leverageFactor = leverageFactor;
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
    public double sizeMultiplication() {
        return sizeMultiplication;
    }

    @Override
    public double leverageFactor() {
        return leverageFactor;
    }

    @Override
    public double profitPerPointForOneContract() {
        return 0;
    }

    @Override
    public double minOrderSize() {
        return 0;
    }

    @Override
    public double minStop() {
        return 0;
    }

    @Override
    public double minLimit() {
        return 0;
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
