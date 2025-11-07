package com.becker.freelance.trading.external.services.management.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.trading.external.services.management.environment.ManagementEnvironmentProvider;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface EntrySignalValidator extends ExternalService {

    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal);
}
