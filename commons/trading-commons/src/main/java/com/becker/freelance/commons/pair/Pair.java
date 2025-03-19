package com.becker.freelance.commons.pair;

import com.becker.freelance.math.Decimal;

import java.time.Duration;
import java.util.List;

public interface Pair {

    static Pair eurUsd1() {
        List<Pair> pairs = allPairs();
        return pairs.stream()
                .filter(p -> "EUR".equals(p.baseCurrency()))
                .filter(p -> "USD".equals(p.counterCurrency()))
                .filter(p -> 1 == p.timeInMinutes())
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No EUR/USD M1 found in " + pairs));
    }

    static Pair fromTechnicalName(String technicalName) {
        return allPairs().stream()
                .filter(p -> technicalName.equals(p.technicalName()))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Could not find Pair with technical name " + technicalName + " in " + allPairs()));
    }

    static List<Pair> allPairs() {
        return PairProvider.allPairs();
    }

    String baseCurrency();

    String counterCurrency();

    Decimal sizeMultiplication();

    Decimal leverageFactor();

    Decimal profitPerPointForOneContract();

    Decimal minOrderSize();

    Decimal minStop();

    Decimal minLimit();

    String technicalName();

    long timeInMinutes();

    default Duration toDuration() {
        return Duration.ofMinutes(timeInMinutes());
    }

    default Decimal priceDifferenceForNProfitInCounterCurrency(Decimal profit, Decimal size) {
        return profit.divide(profitPerPointForOneContract()).divide(size);
    }

    default String shortName() {
        return baseCurrency() + counterCurrency() + "_" + timeInMinutes();
    }

}
