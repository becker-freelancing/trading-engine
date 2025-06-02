package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;

public enum QuantileMarketRegime implements TradeableQuantilMarketRegime {
    UP_LOW_VOLA_033(1),
    UP_LOW_VOLA_066(2),
    UP_LOW_VOLA_1(3),
    UP_HIGH_VOLA_033(4),
    UP_HIGH_VOLA_066(5),
    UP_HIGH_VOLA_1(6),
    SIDE_LOW_VOLA_033(7),
    SIDE_LOW_VOLA_066(8),
    SIDE_LOW_VOLA_1(9),
    SIDE_HIGH_VOLA_033(10),
    SIDE_HIGH_VOLA_066(11),
    SIDE_HIGH_VOLA_1(12),
    DOWN_LOW_VOLA_033(13),
    DOWN_LOW_VOLA_066(14),
    DOWN_LOW_VOLA_1(15),
    DOWN_HIGH_VOLA_033(16),
    DOWN_HIGH_VOLA_066(17),
    DOWN_HIGH_VOLA_1(18);

    private final int id;

    QuantileMarketRegime(int id) {
        this.id = id;
    }

    public static QuantileMarketRegime fromId(int id) {
        for (QuantileMarketRegime regime : QuantileMarketRegime.values()) {
            if (regime.getId() == id) {
                return regime;
            }
        }
        throw new IllegalArgumentException("No QuantileMarketRegime found with id " + id);
    }

    public static QuantileMarketRegime maxQuantile(MarketRegime marketRegime) {
        return switch (marketRegime) {
            case UP_LOW_VOLA -> UP_LOW_VOLA_066;
            case UP_HIGH_VOLA -> UP_HIGH_VOLA_1;
            case SIDE_LOW_VOLA -> SIDE_LOW_VOLA_1;
            case SIDE_HIGH_VOLA -> SIDE_HIGH_VOLA_1;
            case DOWN_LOW_VOLA -> DOWN_LOW_VOLA_1;
            case DOWN_HIGH_VOLA -> DOWN_HIGH_VOLA_1;
        };
    }

    public int getId() {
        return id;
    }
}
