package com.becker.freelance.strategies.creation;

public record StringParameterName(String name) implements ParameterName {
    @Override
    public String getName() {
        return name();
    }
}
