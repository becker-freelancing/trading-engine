package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.management.api.EnvironmentProvider;

public interface Calculator<T, S> {
    public T calculate(EnvironmentProvider environmentProvider, S s);
}
