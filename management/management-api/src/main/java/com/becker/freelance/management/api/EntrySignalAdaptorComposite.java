package com.becker.freelance.management.api;

import com.becker.freelance.commons.signal.EntrySignal;

import java.util.List;

class EntrySignalAdaptorComposite implements EntrySignalAdaptor {

    private final List<EntrySignalAdaptor> adaptors;

    public EntrySignalAdaptorComposite(List<EntrySignalAdaptor> adaptors) {
        this.adaptors = adaptors;
    }

    @Override
    public EntrySignal adapt(EnvironmentProvider environmentProvider, EntrySignal entrySignal) {
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
