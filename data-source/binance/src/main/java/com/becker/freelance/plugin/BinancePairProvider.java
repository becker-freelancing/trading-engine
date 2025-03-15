package com.becker.freelance.plugin;

import com.becker.freelance.commons.pair.AbstractPair;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class BinancePairProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(
                from("BTC", "EUR", 1, "BTC/EUR M1", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 2, "BTC/EUR M2", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 3, "BTC/EUR M3", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 5, "BTC/EUR M5", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 15, "BTC/EUR M15", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 30, "BTC/EUR M30", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 60, "BTC/EUR H1", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 240, "BTC/EUR H4", 1., 1., 2., 1., 1., 1),
                from("BTC", "EUR", 1440, "BTC/EUR D1", 1., 1., 2., 1., 1., 1),

                from("BTC", "USD", 1, "BTC/USD M1", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 2, "BTC/USD M2", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 3, "BTC/USD M3", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 5, "BTC/USD M5", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 15, "BTC/USD M15", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 30, "BTC/USD M30", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 60, "BTC/USD H1", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 240, "BTC/USD H4", 1., 1., 2., 1., 1., 1),
                from("BTC", "USD", 1440, "BTC/USD D1", 1., 1., 2., 1., 1., 1),

                from("ETH", "BTC", 1, "ETH/BTC M1", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 2, "ETH/BTC M2", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 3, "ETH/BTC M3", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 5, "ETH/BTC M5", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 15, "ETH/BTC M15", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 30, "ETH/BTC M30", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 60, "ETH/BTC H1", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 240, "ETH/BTC H4", 1., 1., 2., 1., 1., 1),
                from("ETH", "BTC", 1440, "ETH/BTC D1", 1., 1., 2., 1., 1., 1)
        );
    }

    private Pair from(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Double profitPerPointForOneContract,
                      Double minOrderSize, Double minStop, Double minLimit, Double leverageFactor, long sizeMultiplication) {
        return new AbstractPair(baseCurrency, counterCurrency, timeInMinutes, technicalName, new Decimal(profitPerPointForOneContract), new Decimal(minOrderSize),
                new Decimal(minStop), new Decimal(minLimit), new Decimal(leverageFactor), new Decimal(sizeMultiplication));
    }
}
