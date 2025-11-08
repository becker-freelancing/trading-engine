package com.becker.freelance.trading.external.services.management.adaption;

import com.becker.freelance.trading.external.services.registry.NoParamsExternalServiceBuilder;

import java.util.List;
import java.util.ServiceLoader;

public interface EntrySignalAdaptorBuilder extends NoParamsExternalServiceBuilder<EntrySignalAdaptor> {

    @Override
    default EntrySignalAdaptor build() {
        List<EntrySignalAdaptor> adaptors = ServiceLoader.load(EntrySignalAdaptor.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList();
        return new EntrySignalAdaptorComposite(adaptors);
    }

}
