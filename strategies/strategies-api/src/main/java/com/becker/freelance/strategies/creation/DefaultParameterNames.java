package com.becker.freelance.strategies.creation;

public enum DefaultParameterNames implements ParameterName {
    SIZE("size"),
    TAKE_PROFIT("takeProfit"),
    STOP_LOSS("stopLoss");

    private final String name;

    DefaultParameterNames(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
