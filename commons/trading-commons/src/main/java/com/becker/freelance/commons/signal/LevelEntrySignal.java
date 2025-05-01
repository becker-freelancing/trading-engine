package com.becker.freelance.commons.signal;

import com.becker.freelance.math.Decimal;

public interface LevelEntrySignal extends EntrySignal {

    public Decimal stopLevel();

    public Decimal limitLevel();

    public default Decimal stopInPoints() {
        return getOpenPriceForDirection().subtract(stopLevel()).abs();
    }

    public default Decimal limitInPoints() {
        return getOpenPriceForDirection().subtract(limitLevel()).abs();
    }

    @Override
    default void visit(EntrySignalVisitor visitor) {
        visitor.accept(this);
    }
}
