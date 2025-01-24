package com.becker.freelance.commons.pair;

import java.util.Objects;

public record AbstractPair(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, double profitPerPointForOneContract,
                           double minOrderSize, double minStop, double minLimit, double leverageFactor, double sizeMultiplication) implements Pair {

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AbstractPair that = (AbstractPair) object;
        return Double.compare(sizeMultiplication, that.sizeMultiplication) == 0 && Double.compare(leverageFactor, that.leverageFactor) == 0 && Double.compare(profitPerPointForOneContract, that.profitPerPointForOneContract) == 0 && Double.compare(minOrderSize, that.minOrderSize) == 0 && Double.compare(minStop, that.minStop) == 0 && Double.compare(minLimit, that.minLimit) == 0 && timeInMinutes == that.timeInMinutes && Objects.equals(baseCurrency, that.baseCurrency) && Objects.equals(counterCurrency, that.counterCurrency) && Objects.equals(technicalName, that.technicalName);
    }

}
