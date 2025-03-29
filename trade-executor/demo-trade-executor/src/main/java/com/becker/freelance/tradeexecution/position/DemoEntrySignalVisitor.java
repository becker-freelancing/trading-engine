package com.becker.freelance.tradeexecution.position;

import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionFactory;
import com.becker.freelance.commons.signal.AmountEntrySignal;
import com.becker.freelance.commons.signal.DistanceEntrySignal;
import com.becker.freelance.commons.signal.EntrySignalVisitor;
import com.becker.freelance.commons.signal.LevelEntrySignal;

public class DemoEntrySignalVisitor implements EntrySignalVisitor {

    private final PositionFactory positionFactory;
    private Position position;

    public DemoEntrySignalVisitor(PositionFactory positionFactory) {
        this.positionFactory = positionFactory;
    }

    @Override
    public void accept(AmountEntrySignal entrySignal) {
        position = positionFactory.createStopLimitPosition(entrySignal);
    }

    @Override
    public void accept(DistanceEntrySignal entrySignal) {
        position = positionFactory.createStopLimitPosition(entrySignal);
    }

    @Override
    public void accept(LevelEntrySignal entrySignal) {
        position = positionFactory.createStopLimitPosition(entrySignal);
    }

    public Position getPosition() {
        return position;
    }
}
