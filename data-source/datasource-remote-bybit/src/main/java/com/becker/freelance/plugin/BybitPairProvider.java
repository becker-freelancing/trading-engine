package com.becker.freelance.plugin;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.AbstractPair;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class BybitPairProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(
                from("BTC", "USDT", 1, "BTC/USDT M1", 1., 0.0001, 60., 0.05, 2., 2),
                from("BTC", "USDT", 5, "BTC/USDT M5", 1., 0.0001, 60., 0.05, 2., 2),

                from("ETH", "USDT", 1, "ETH/USDT M1", 1., 0.0001, 60., 0.05, 2., 2),
                from("ETH", "USDT", 5, "ETH/USDT M5", 1., 0.0001, 60., 0.05, 2., 2),

                from("ETH", "PERP", 1, "ETH/USDC M1", 1., 0.0001, 60., 0.05, 1/ 10., 1),
                from("ETH", "PERP", 5, "ETH/USDC M5", 1., 0.0001, 60., 0.05, 1 / 10., 1),

                from("USDT", "EUR", 1, "USDT/EUR M1", 1., 0.0001, 60., 0.05, 2., 2),
                from("EUR", "USDT", 1, "EUR/USDT M1", 1., 0.0001, 60., 0.05, 2., 2)

        );
    }

    private Pair from(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Double profitPerPointForOneContract,
                      Double minOrderSize, Double minStop, Double minLimit, Double leverageFactor, long sizeMultiplication) {
        return new ByBitPair(baseCurrency, counterCurrency, timeInMinutes, technicalName, new Decimal(profitPerPointForOneContract), new Decimal(minOrderSize),
                new Decimal(minStop), new Decimal(minLimit), new Decimal(leverageFactor), new Decimal(sizeMultiplication));
    }

    private static class ByBitPair extends AbstractPair {

        public ByBitPair(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Decimal profitPerPointForOneContract, Decimal minOrderSize, Decimal minStop, Decimal minLimit, Decimal leverageFactor, Decimal sizeMultiplication) {
            super(baseCurrency, counterCurrency, timeInMinutes, technicalName, profitPerPointForOneContract, minOrderSize, minStop, minLimit, leverageFactor, sizeMultiplication);
        }

        @Override
        public boolean isExecutableInAppMode(AppMode appMode) {
            return new BybitDemoAppMode().isEqual(appMode);
        }


        @Override
        public String getUsdString() {
            return "USDT";
        }
    }
}
