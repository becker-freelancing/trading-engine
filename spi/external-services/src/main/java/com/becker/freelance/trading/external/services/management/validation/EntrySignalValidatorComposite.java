package com.becker.freelance.trading.external.services.management.validation;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.trading.external.services.management.environment.ManagementEnvironmentProvider;

import java.util.List;

public class EntrySignalValidatorComposite implements EntrySignalValidator {

    private final List<EntrySignalValidator> validators;
    private final CompositeStrategy compositeStrategy;

    private final CompositeFunction compositeFunction;

    public EntrySignalValidatorComposite(List<EntrySignalValidator> validators, CompositeStrategy compositeStrategy) {
        this.validators = validators;
        this.compositeStrategy = compositeStrategy;
        this.compositeFunction = getCompositeFunction();
    }

    private CompositeFunction getCompositeFunction() {
        return switch (compositeStrategy) {
            case ANY_MATCH -> this::anyMatch;
            case ALL_MATCH -> this::allMatch;
        };
    }

    @Override
    public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        return compositeFunction.isValidToExecute(environmentProvider, entrySignal);
    }

    private boolean allMatch(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        for (EntrySignalValidator validator : validators) {
            boolean validToExecute = validator.isValidToExecute(environmentProvider, entrySignal);
            if (!validToExecute) {
                return false;
            }
        }
        return true;
    }

    private boolean anyMatch(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        for (EntrySignalValidator validator : validators) {
            boolean validToExecute = validator.isValidToExecute(environmentProvider, entrySignal);
            if (validToExecute) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "EntrySignalValidators with CompositeStrategy " + compositeStrategy + ":\n" + String.join("\n\t* ", validators.stream().map(Object::getClass).map(Class::getName).toList());
    }

    private static interface CompositeFunction {
        public boolean isValidToExecute(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal);
    }
}
