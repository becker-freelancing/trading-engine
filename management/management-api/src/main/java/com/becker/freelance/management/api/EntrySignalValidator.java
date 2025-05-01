package com.becker.freelance.management.api;

import com.becker.freelance.commons.signal.EntrySignal;

public interface EntrySignalValidator {

    public boolean isValidToExecute(EnvironmentProvider environmentProvider, EntrySignal entrySignal);
}
