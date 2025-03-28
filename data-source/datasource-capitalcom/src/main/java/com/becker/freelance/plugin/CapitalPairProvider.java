package com.becker.freelance.plugin;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.AbstractPair;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class CapitalPairProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(
                from("BTC", "USD", 1, "BTC/USD M1", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 2, "BTC/USD M2", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 3, "BTC/USD M3", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 5, "BTC/USD M5", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 15, "BTC/USD M15", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 30, "BTC/USD M30", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 60, "BTC/USD H1", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 240, "BTC/USD H4", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USD", 1440, "BTC/USD D1", 1., 0.0001, 60., 0.05, 2., 2),

                from("ETH", "USD", 1, "ETH/USD M1", 1., 0.0001, 60., 0.05, 2., 2)

        );
    }

    private Pair from(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Double profitPerPointForOneContract,
                      Double minOrderSize, Double minStop, Double minLimit, Double leverageFactor, long sizeMultiplication) {
        return new CapitalPair(baseCurrency, counterCurrency, timeInMinutes, technicalName, new Decimal(profitPerPointForOneContract), new Decimal(minOrderSize),
                new Decimal(minStop), new Decimal(minLimit), new Decimal(leverageFactor), new Decimal(sizeMultiplication));
    }

    private static class CapitalPair extends AbstractPair {

        public CapitalPair(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Decimal profitPerPointForOneContract, Decimal minOrderSize, Decimal minStop, Decimal minLimit, Decimal leverageFactor, Decimal sizeMultiplication) {
            super(baseCurrency, counterCurrency, timeInMinutes, technicalName, profitPerPointForOneContract, minOrderSize, minStop, minLimit, leverageFactor, sizeMultiplication);
        }

        @Override
        public boolean isExecutableInAppMode(AppMode appMode) {
            return new CapitalDemoAppMode().isEqual(appMode);
        }
    }
}
