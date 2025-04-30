package com.becker.freelance.management.commons.validation;

import com.becker.freelance.management.api.EnvironmentProvider;

public interface Validator<S> {

    public boolean isValid(EnvironmentProvider environmentProvider, S s);
}
