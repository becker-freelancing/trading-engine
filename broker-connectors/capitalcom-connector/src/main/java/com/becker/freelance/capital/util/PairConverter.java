package com.becker.freelance.capital.util;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;

public class PairConverter {

    public Pair convert(String capitalPair, String capitalResolution) {
        int durationInMinutes = mapCapitalResolution(capitalResolution);
        return PairProvider.allPairs().stream()
                .filter(pair -> pair.timeInMinutes() == durationInMinutes)
                .filter(pair -> capitalPair.contains(pair.baseCurrency()))
                .filter(pair -> capitalPair.contains(pair.counterCurrency()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Could not map pair " + capitalPair + " with resolution " + capitalResolution));
    }

    private int mapCapitalResolution(String capitalResolution) {
        return switch (capitalResolution) {
            case "MINUTE" -> 1;
            case "MINUTE_5" -> 5;
            case "MINUTE_15" -> 15;
            case "MINUTE_30" -> 30;
            case "HOUR" -> 60;
            case "HOUR_4" -> 240;
            case "DAY" -> 1440;
            default -> throw new IllegalArgumentException("Could not convert Resolution " + capitalResolution);
        };
    }

    public String convert(Pair pair) {
        return pair.baseCurrency() + pair.counterCurrency();
    }

    public String convertResolution(Pair pair) {
        return switch ((int) pair.timeInMinutes()) {
            case 1 -> "MINUTE";
            case 5 -> "MINUTE_5";
            case 15 -> "MINUTE_15";
            case 30 -> "MINUTE_30";
            case 60 -> "HOUR";
            case 240 -> "HOUR_4";
            case 1440 -> "DAY";
            default -> throw new IllegalArgumentException("Could not convert Resolution " + pair.technicalName());
        };
    }
}
