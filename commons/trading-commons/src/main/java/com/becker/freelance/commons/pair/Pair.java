package com.becker.freelance.commons.pair;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.math.Decimal;

import java.time.Duration;
import java.util.List;

public interface Pair {

    static Pair eurUsd1() {
        List<Pair> pairs = allPairs();
        return pairs.stream()
                .filter(Pair::isEuroBaseCurrency)
                .filter(Pair::isUsdCounterCurrency)
                .filter(p -> 1 == p.timeInMinutes())
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No EUR/USD M1 found in " + pairs));
    }


    static Pair usdEur1() {
        List<Pair> pairs = allPairs();
        return pairs.stream()
                .filter(Pair::isUsdBaseCurrency)
                .filter(Pair::isEuroCounterCurrency)
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

    boolean isExecutableInAppMode(AppMode appMode);

    default boolean isEuroCounterCurrency() {
        return getEurString().equals(counterCurrency());
    }

    default String getEurString() {
        return "EUR";
    }

    default boolean isEuroBaseCurrency() {
        return getEurString().equals(baseCurrency());
    }

    default boolean isUsdCounterCurrency() {
        return getUsdString().equals(counterCurrency());
    }

    default String getUsdString() {
        return "USD";
    }

    default boolean isUsdBaseCurrency() {
        return getUsdString().equals(baseCurrency());
    }
}
