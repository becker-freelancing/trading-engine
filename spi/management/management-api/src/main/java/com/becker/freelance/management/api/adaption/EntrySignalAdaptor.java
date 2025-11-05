package com.becker.freelance.management.api.adaption;

import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;

public interface EntrySignalAdaptor {

    public EntrySignalBuilder adapt(ManagementEnvironmentProvider environmentProvider, EntrySignalBuilder entrySignal);
}
