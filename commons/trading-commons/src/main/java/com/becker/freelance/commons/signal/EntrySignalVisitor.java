package com.becker.freelance.commons.signal;

public interface EntrySignalVisitor {
    public void accept(AmountEntrySignal entrySignal);

    public void accept(DistanceEntrySignal entrySignal);

    public void accept(LevelEntrySignal entrySignal);
}
