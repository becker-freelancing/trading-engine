package com.becker.freelance.management.api.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;

public interface EntrySignalValidator {

    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal);
}
