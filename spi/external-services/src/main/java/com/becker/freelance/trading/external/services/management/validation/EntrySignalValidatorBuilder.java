package com.becker.freelance.trading.external.services.management.validation;

import com.becker.freelance.trading.external.services.registry.ParamsExternalServiceBuilder;

import java.util.List;
import java.util.ServiceLoader;

public interface EntrySignalValidatorBuilder extends ParamsExternalServiceBuilder<CompositeStrategy, EntrySignalValidator> {

    @Override
    default EntrySignalValidator build(CompositeStrategy compositeStrategy) {
        List<EntrySignalValidator> validators = ServiceLoader.load(EntrySignalValidator.class).stream()
                .map(ServiceLoader.Provider::get)
                .toList();
        return new EntrySignalValidatorComposite(validators, compositeStrategy);
    }

    @Override
    default Class<? extends EntrySignalValidator> getServiceClass() {
        return EntrySignalValidator.class;
    }
}
