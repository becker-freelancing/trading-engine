package com.becker.freelance.commons.signal;

import com.becker.freelance.math.Decimal;

public interface AmountEntrySignal extends EntrySignal {

    public Decimal stopAmount();

    public Decimal limitAmount();

    @Override
    default void visit(EntrySignalVisitor visitor) {
        visitor.accept(this);
    }
}
