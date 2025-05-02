package com.becker.freelance.management.api.adaption;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;

import java.util.List;

public class EntrySignalAdaptorComposite implements EntrySignalAdaptor {

    private final List<EntrySignalAdaptor> adaptors;

    public EntrySignalAdaptorComposite(List<EntrySignalAdaptor> adaptors) {
        this.adaptors = adaptors;
    }

    @Override
    public EntrySignal adapt(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        for (EntrySignalAdaptor adaptor : adaptors) {
            entrySignal = adaptor.adapt(environmentProvider, entrySignal);
        }
        return entrySignal;
    }

    @Override
    public String toString() {
        return "EntrySignalAdaptors:\n" + String.join("\n\t* ", adaptors.stream().map(Object::getClass).map(Class::getName).toList());
    }
}
