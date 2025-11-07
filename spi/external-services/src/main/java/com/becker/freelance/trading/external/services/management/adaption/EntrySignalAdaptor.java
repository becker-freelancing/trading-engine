package com.becker.freelance.trading.external.services.management.adaption;

import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.trading.external.services.management.environment.ManagementEnvironmentProvider;
import com.becker.freelance.trading.external.services.registry.ExternalService;

public interface EntrySignalAdaptor extends ExternalService {

    public EntrySignalBuilder adapt(ManagementEnvironmentProvider environmentProvider, EntrySignalBuilder entrySignal);
}
