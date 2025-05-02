package com.becker.freelance.management.api.adaption;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;

public interface EntrySignalAdaptor {

    public EntrySignal adapt(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal);
}
