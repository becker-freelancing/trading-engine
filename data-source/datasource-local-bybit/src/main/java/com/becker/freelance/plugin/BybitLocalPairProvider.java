package com.becker.freelance.plugin;

import com.becker.freelance.commons.app.AppMode;
import com.becker.freelance.commons.pair.AbstractPair;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class BybitLocalPairProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(
                from("ETH", "USDT", 1, "ETH/USDT M1", 1., 1., 2., 1., 1., 1),
                from("EUR", "USD", 1, "EUR/USD M1", 1., 1., 2., 1., 1., 1)
        );
    }

    private Pair from(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Double profitPerPointForOneContract,
                      Double minOrderSize, Double minStop, Double minLimit, Double leverageFactor, long sizeMultiplication) {
        return new BinancePair(baseCurrency, counterCurrency, timeInMinutes, technicalName, new Decimal(profitPerPointForOneContract), new Decimal(minOrderSize),
                new Decimal(minStop), new Decimal(minLimit), new Decimal(leverageFactor), new Decimal(sizeMultiplication));
    }

    private static class BinancePair extends AbstractPair {

        public BinancePair(String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Decimal profitPerPointForOneContract, Decimal minOrderSize, Decimal minStop, Decimal minLimit, Decimal leverageFactor, Decimal sizeMultiplication) {
            super(baseCurrency, counterCurrency, timeInMinutes, technicalName, profitPerPointForOneContract, minOrderSize, minStop, minLimit, leverageFactor, sizeMultiplication);
        }

        @Override
        public boolean isExecutableInAppMode(AppMode appMode) {
            return new BybitLocalDemoAppMode().isEqual(appMode);
        }
    }
}
