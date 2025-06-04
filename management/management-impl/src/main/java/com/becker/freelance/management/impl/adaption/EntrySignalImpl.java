package com.becker.freelance.management.impl.adaption;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class EntrySignalImpl implements EntrySignal {

    private final EntrySignal levelEntrySignal;
    private final Decimal size;

    public EntrySignalImpl(EntrySignal entrySignal, Decimal size) {
        this.levelEntrySignal = entrySignal;
        this.size = size;
    }

    @Override
    public Decimal stopLevel() {
        return levelEntrySignal.stopLevel();
    }

    @Override
    public Decimal limitLevel() {
        return levelEntrySignal.limitLevel();
    }

    @Override
    public Decimal size() {
        return size;
    }

    @Override
    public Direction direction() {
        return levelEntrySignal.direction();
    }

    @Override
    public Pair pair() {
        return levelEntrySignal.pair();
    }

    @Override
    public TimeSeriesEntry openPrice() {
        return levelEntrySignal.openPrice();
    }

    @Override
    public PositionBehaviour positionBehaviour() {
        return levelEntrySignal.positionBehaviour();
    }

    @Override
    public TradeableQuantilMarketRegime openMarketRegime() {
        return levelEntrySignal.openMarketRegime();
    }
}
