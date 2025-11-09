package com.becker.freelance.strategies.creation;

import com.becker.freelance.trading.external.services.strategies.ParameterName;

public record StringParameterName(String name) implements ParameterName {
    @Override
    public String getName() {
        return name();
    }
}
