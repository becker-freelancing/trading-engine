package com.becker.freelance.management.impl.adaption;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class LevelEntrySignalImpl implements LevelEntrySignal {

    private final LevelEntrySignal levelEntrySignal;
    private final Decimal size;

    public LevelEntrySignalImpl(LevelEntrySignal levelEntrySignal, Decimal size) {
        this.levelEntrySignal = levelEntrySignal;
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
    public PositionType positionType() {
        return levelEntrySignal.positionType();
    }

    @Override
    public TradeableQuantilMarketRegime openMarketRegime() {
        return levelEntrySignal.openMarketRegime();
    }
}
