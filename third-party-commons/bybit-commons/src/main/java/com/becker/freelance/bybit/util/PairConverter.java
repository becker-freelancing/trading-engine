package com.becker.freelance.bybit.util;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;

import java.util.Optional;

public class PairConverter {

    public Optional<Pair> convert(String bybitPair, String bybitResolution) {
        int durationInMinutes = mapBybitResolution(bybitResolution);
        return PairProvider.allPairs().stream()
                .filter(pair -> pair.timeInMinutes() == durationInMinutes)
                .filter(pair -> bybitPair.contains(pair.baseCurrency()))
                .filter(pair -> bybitPair.contains(pair.counterCurrency()))
                .findFirst();
    }

    private int mapBybitResolution(String bybitResolution) {
        return Integer.parseInt(bybitResolution);
    }

    public String convert(Pair pair) {
        return pair.baseCurrency() + pair.counterCurrency();
    }

    public String convertResolution(Pair pair) {
        return String.valueOf(pair.timeInMinutes());
    }
}
