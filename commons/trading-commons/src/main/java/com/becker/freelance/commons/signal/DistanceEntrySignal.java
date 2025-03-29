package com.becker.freelance.commons.signal;

import com.becker.freelance.math.Decimal;

public interface DistanceEntrySignal extends EntrySignal {

    public Decimal getStopDistance();

    public Decimal getLimitDistance();

    @Override
    default void visit(EntrySignalVisitor visitor) {
        visitor.accept(this);
    }
}
