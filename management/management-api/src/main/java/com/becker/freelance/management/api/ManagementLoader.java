package com.becker.freelance.management.api;

import java.util.List;
import java.util.ServiceLoader;

public class ManagementLoader {

    public EntrySignalAdaptor findEntrySignalAdaptor() {

        return new EntrySignalAdaptorComposite(loadAll(EntrySignalAdaptor.class));
    }

    public EntrySignalValidator findEntrySignalValidator(CompositeStrategy compositeStrategy) {

        return new EntrySignalValidatorComposite(loadAll(EntrySignalValidator.class), compositeStrategy);
    }

    private <T> List<T> loadAll(Class<T> clazz) {
        return ServiceLoader.load(clazz).stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }
}
