package com.becker.freelance.tradeexecution.calculation.mock;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.pair.PairProvider;

import java.util.List;

public class PairMockProvider implements PairProvider {
    @Override
    public List<Pair> get() {
        return List.of(new PairMock("EUR", "USD", 100_000, 0.0333));
    }
}
