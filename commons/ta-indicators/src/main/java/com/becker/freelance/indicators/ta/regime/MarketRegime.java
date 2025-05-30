package com.becker.freelance.indicators.ta.regime;

public enum MarketRegime {
    DOWN_HIGH_VOLA(1),
    DOWN_LOW_VOLA(2),
    SIDE_HIGH_VOLA(3),
    SIDE_LOW_VOLA(4),
    UP_HIGH_VOLA(5),
    UP_LOW_VOLA(6);

    private final int id;

    MarketRegime(int id) {
        this.id = id;
    }

    public static MarketRegime fromId(int id) {
        for (MarketRegime regime : MarketRegime.values()) {
            if (regime.getId() == id) {
                return regime;
            }
        }
        throw new IllegalArgumentException("Could not find regime with id " + id);
    }

    public int getId() {
        return id;
    }
}
