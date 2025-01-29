package com.becker.freelance.commons.pair;

import com.becker.freelance.math.Decimal;

import java.util.Objects;

public record AbstractPair(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Decimal profitPerPointForOneContract,
                           Decimal minOrderSize, Decimal minStop, Decimal minLimit, Decimal leverageFactor, Decimal sizeMultiplication) implements Pair {


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
}
