package com.becker.freelance.management.commons.validation;

import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;

public interface Validator<S> {

    public boolean isValid(ManagementEnvironmentProvider environmentProvider, S s);
}
