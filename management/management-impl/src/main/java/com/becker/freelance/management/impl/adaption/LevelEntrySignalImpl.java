package com.becker.freelance.management.impl.adaption;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
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
    public Decimal getSize() {
        return size;
    }

    @Override
    public Direction getDirection() {
        return levelEntrySignal.getDirection();
    }

    @Override
    public Pair getPair() {
        return levelEntrySignal.getPair();
    }

    @Override
    public TimeSeriesEntry getOpenPrice() {
        return levelEntrySignal.getOpenPrice();
    }

    @Override
    public PositionType positionType() {
        return levelEntrySignal.positionType();
    }
}
