package com.becker.freelance.plugin.pair;

import com.becker.freelance.commons.pair.AbstractPair;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;
import com.becker.freelance.math.Decimal;

import java.util.List;

public class KrakenPairProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(
                from("ETH", "EUR", 1, "ETH/EUR M1", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 5, "ETH/EUR M5", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 15, "ETH/EUR M15", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 30, "ETH/EUR M30", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 60, "ETH/EUR H1", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 240, "ETH/EUR H4", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 720, "ETH/EUR H12", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("ETH", "EUR", 1440, "ETH/EUR T1", 1.0, 0.2, 38.6, 1., 0.5, 1),
                from("PAXG", "USD", 1, "GLD/USD M1", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 5, "GLD/USD M5", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 15, "GLD/USD M15", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 30, "GLD/USD M30", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 60, "GLD/USD H1", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 240, "GLD/USD H4", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 720, "GLD/USD H12", 10., 0.1, 1., 1., 0.5, 1),
                from("PAXG", "USD", 1440, "GLD/USD T1", 10., 0.1, 1., 1., 0.5, 1),
                from("XBT", "EUR", 1, "BTC/EUR M1", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 5, "BTC/EUR M5", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 15, "BTC/EUR M15", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 30, "BTC/EUR M30", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 60, "BTC/EUR H1", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 240, "BTC/EUR H4", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 720, "BTC/EUR H12", 1., 0.01, 1039., 1., 0.5, 1),
                from("XBT", "EUR", 1440, "BTC/EUR T1", 1., 0.01, 1039., 1., 0.5, 1),
                from("EUR", "USD", 1, "EUR/USD M1", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 5, "EUR/USD M5", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 15, "EUR/USD M15", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 30, "EUR/USD M30", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 60, "EUR/USD H1", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 240, "EUR/USD H4", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 720, "EUR/USD H12", 100_000., 1., 2., 1., 0.0333, 100_000),
                from("EUR", "USD", 1440, "EUR/USD T1", 100_000., 1., 2., 1., 0.0333, 100_000)
        );
    }

    private Pair from (String baseCurrency, String counterCurrency, long timeInMinutes, String technicalName, Double profitPerPointForOneContract,
                       Double minOrderSize, Double minStop, Double minLimit, Double leverageFactor, long sizeMultiplication){
        return new AbstractPair(baseCurrency, counterCurrency, timeInMinutes, technicalName, new Decimal(profitPerPointForOneContract), new Decimal(minOrderSize),
                new Decimal(minStop), new Decimal(minLimit), new Decimal(leverageFactor), new Decimal(sizeMultiplication));
    }
}
