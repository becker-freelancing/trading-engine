package com.becker.freelance.commons.pair;

import java.time.Duration;
import java.util.List;
import java.util.ServiceLoader;

public interface Pair {

    public static Pair eurUsd1(){
        List<Pair> pairs = allPairs();
        return pairs.stream()
                .filter(p -> "EUR".equals(p.baseCurrency()))
                .filter(p -> "USD".equals(p.counterCurrency()))
                .filter(p -> 1 == p.timeInMinutes())
                .findAny()
                .orElseThrow(() -> new IllegalStateException("No EUR/USD M1 found in " + pairs));
    }

    public static Pair fromTechnicalName(String technicalName){
        return allPairs().stream()
                .filter(p -> technicalName.equals(p.technicalName()))
                .findAny().orElseThrow(() -> new IllegalArgumentException("Could not find Pair with technical name " + technicalName));
    }

    public static List<Pair> allPairs(){
        return PairProvider.allPairs();
    }

    public String baseCurrency();
    public String counterCurrency();
    public double sizeMultiplication();
    public double leverageFactor();
    public double profitPerPointForOneContract();
    public double minOrderSize();
    public double minStop();
    public double minLimit();
    public String technicalName();
    public default Duration toDuration(){
        return Duration.ofMinutes(timeInMinutes());
    }

    public long timeInMinutes();
}
