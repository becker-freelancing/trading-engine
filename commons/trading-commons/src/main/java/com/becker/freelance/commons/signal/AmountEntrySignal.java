package com.becker.freelance.commons.signal;

import com.becker.freelance.math.Decimal;

public interface AmountEntrySignal extends EntrySignal {

    public Decimal getStopAmount();

    public Decimal getLimitAmount();

    @Override
    default void visit(EntrySignalVisitor visitor) {
        visitor.accept(this);
    }
}
