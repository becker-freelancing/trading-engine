package com.becker.freelance.plugin;

import com.becker.freelance.commons.pair.AbstractPair;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class HistDataPairProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(
                from("EUR", "USD", 1, "EUR/USD M1", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 5, "EUR/USD M5", 100_000., 1., 2., 1., 0.0333, 100_000)
        );
    }

    private Pair from(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Double profitPerPointForOneContract,
                      Double minOrderSize, Double minStop, Double minLimit, Double leverageFactor, long sizeMultiplication) {
        return new AbstractPair(baseCurrency, counterCurrency, timeInMinutes, technicalName, new Decimal(profitPerPointForOneContract), new Decimal(minOrderSize),
                new Decimal(minStop), new Decimal(minLimit), new Decimal(leverageFactor), new Decimal(sizeMultiplication));
    }
}
