package com.becker.freelance.strategies.creation;

public enum DefaultParameterNames implements ParameterName {
    SIZE("size"),
    TAKE_PROFIT("takeProfit"),
    STOP_LOSS("stopLoss"),
    PERIOD("period"),
    SMA_PERIOD("smaPeriod"),
    RSI_PERIOD("rsiPeriod"),
    EMA_PERIOD("emaPeriod"),
    ATR_PERIOD("atrPeriod"),
    STOCH_K_PERIOD("stochKPeriod"),
    MACD_SHORT_PERIOD("macdShortPeriod"),
    MACD_LONG_PERIOD("macdLongPeriod"),
    SHORT_MA_PERIOD("shortMaPeriod"),
    MID_MA_PERIOD("midMaPeriod"),
    LONG_MA_PERIOD("longMaPeriod"),
    SWING_HIGH_LOW_ORDER("swingHighLowOrder"),
    SWING_HIGH_LOW_MAX_AGE("swingHighLowMaxAge"),
    SIGNAL_LINE_PERIOD("signalLinePeriod"),
    TRAILING_STOP_ORDER("trailingStopOrder");

    private final String name;

    DefaultParameterNames(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }


}
