package com.becker.freelance.management.api;

import com.becker.freelance.commons.signal.EntrySignal;

public interface EntrySignalAdaptor {

    public EntrySignal adapt(EnvironmentProvider environmentProvider, EntrySignal entrySignal);
}
