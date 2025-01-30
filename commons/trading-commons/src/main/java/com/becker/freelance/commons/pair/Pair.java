package com.becker.freelance.commons.pair;

import java.time.Duration;
import java.util.List;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.math.Decimal;

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
    public Decimal sizeMultiplication();
    public Decimal leverageFactor();
    public Decimal profitPerPointForOneContract();
    public Decimal minOrderSize();
    public Decimal minStop();
    public Decimal minLimit();
    public String technicalName();
    public long timeInMinutes();
    public default Duration toDuration(){
        return Duration.ofMinutes(timeInMinutes());
    }
    public default Decimal priceDifferenceForNProfitInCounterCurrency(Decimal profit){
        return profit.divide(profitPerPointForOneContract());
    }

}
