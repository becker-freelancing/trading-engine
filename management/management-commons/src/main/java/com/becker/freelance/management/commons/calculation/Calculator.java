package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;

public interface Calculator<T, S> {
    public T calculate(ManagementEnvironmentProvider environmentProvider, S s);
}
