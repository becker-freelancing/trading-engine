package com.becker.freelance.commons.signal;

import com.becker.freelance.math.Decimal;

public interface LevelEntrySignal extends EntrySignal {

    public Decimal getStopLevel();

    public Decimal getLimitLevel();

    @Override
    default void visit(EntrySignalVisitor visitor) {
        visitor.accept(this);
    }
}
