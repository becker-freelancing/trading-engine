package com.becker.freelance.management.api;

import com.becker.freelance.commons.signal.EntrySignal;

import java.util.List;

class EntrySignalValidatorComposite implements EntrySignalValidator {

    private final List<EntrySignalValidator> validators;
    private final CompositeStrategy compositeStrategy;

    public EntrySignalValidatorComposite(List<EntrySignalValidator> validators, CompositeStrategy compositeStrategy) {
        this.validators = validators;
        this.compositeStrategy = compositeStrategy;
    }

    @Override
    public boolean isValidToExecute(EnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return switch (compositeStrategy) {
            case ALL_MATCH ->
                    validators.stream().allMatch(validator -> validator.isValidToExecute(environmentProvider, entrySignal));
            case ANY_MATCH ->
                    validators.stream().anyMatch(validator -> validator.isValidToExecute(environmentProvider, entrySignal));
        };
    }


    @Override
    public String toString() {
        return "EntrySignalValidators:\n" + String.join("\n\t* ", validators.stream().map(Object::getClass).map(Class::getName).toList());
    }
}
