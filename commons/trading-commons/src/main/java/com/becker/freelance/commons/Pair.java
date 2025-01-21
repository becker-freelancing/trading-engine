package com.becker.freelance.commons;

import java.time.Duration;

public enum Pair {
    ETHEUR_1("ETH", "EUR", 1, "ETH/EUR M1", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_5("ETH", "EUR", 5, "ETH/EUR M5", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_15("ETH", "EUR", 15, "ETH/EUR M15", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_30("ETH", "EUR", 30, "ETH/EUR M30", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_60("ETH", "EUR", 60, "ETH/EUR H1", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_240("ETH", "EUR", 240, "ETH/EUR H4", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_720("ETH", "EUR", 720, "ETH/EUR H12", 1.0, 0.2, 38.6, 1, 0.5, 1),
    ETHEUR_1440("ETH", "EUR", 1440, "ETH/EUR T1", 1.0, 0.2, 38.6, 1, 0.5, 1),
    PAXGUSD_1("PAXG", "USD", 1, "GLD/USD M1", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_5("PAXG", "USD", 5, "GLD/USD M5", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_15("PAXG", "USD", 15, "GLD/USD M15", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_30("PAXG", "USD", 30, "GLD/USD M30", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_60("PAXG", "USD", 60, "GLD/USD H1", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_240("PAXG", "USD", 240, "GLD/USD H4", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_720("PAXG", "USD", 720, "GLD/USD H12", 10, 0.1, 1, 1, 0.05, 1),
    PAXGUSD_1440("PAXG", "USD", 1440, "GLD/USD T1", 10, 0.1, 1, 1, 0.05, 1),
    XBTEUR_1("XBT", "EUR", 1, "BTC/EUR M1", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_5("XBT", "EUR", 5, "BTC/EUR M5", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_15("XBT", "EUR", 15, "BTC/EUR M15", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_30("XBT", "EUR", 30, "BTC/EUR M30", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_60("XBT", "EUR", 60, "BTC/EUR H1", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_240("XBT", "EUR", 240, "BTC/EUR H4", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_720("XBT", "EUR", 720, "BTC/EUR H12", 1, 0.01, 1039, 1, 0.5, 1),
    XBTEUR_1440("XBT", "EUR", 1440, "BTC/EUR T1", 1, 0.01, 1039, 1, 0.5, 1),
    EURUSD_1("EUR", "USD", 1, "EUR/USD M1", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_5("EUR", "USD", 5, "EUR/USD M5", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_15("EUR", "USD", 15, "EUR/USD M15", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_30("EUR", "USD", 30, "EUR/USD M30", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_60("EUR", "USD", 60, "EUR/USD H1", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_240("EUR", "USD", 240, "EUR/USD H4", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_720("EUR", "USD", 720, "EUR/USD H12", 10, 1, 2, 1, 0.0333, 100_000),
    EURUSD_1440("EUR", "USD", 1440, "EUR/USD T1", 10, 1, 2, 1, 0.0333, 100_000);

    private final String baseCurrency;
    private final String counterCurrency;
    private final int timeInMinutes;
    private final String technicalName;
    private final double profitPerPoint;
    private final double minOrderSize;
    private final double minStop;
    private final double minLimit;
    private final double leverageFactor;
    private final double sizeMultiplication;

    Pair(String baseCurrency, String counterCurrency, int timeInMinutes, String technicalName, double profitPerPoint,
         double minOrderSize, double minStop, double minLimit, double leverageFactor, double sizeMultiplication) {
        this.baseCurrency = baseCurrency;
        this.counterCurrency = counterCurrency;
        this.timeInMinutes = timeInMinutes;
        this.technicalName = technicalName;
        this.profitPerPoint = profitPerPoint;
        this.minOrderSize = minOrderSize;
        this.minStop = minStop;
        this.minLimit = minLimit;
        this.leverageFactor = leverageFactor;
        this.sizeMultiplication = sizeMultiplication;
    }

    public static Pair fromName(String pair) {
        for (Pair value : Pair.values()) {
            if (value.getFilename().replace(".csv", "").equals(pair)){
                return value;
            }
        }
return null;
    }

    public Duration toDuration() {
        return Duration.ofMinutes(timeInMinutes);
    }

    public double getProfitPerPointForOneContract() {
        return profitPerPoint;
    }

    public String getFilename() {
        return baseCurrency + counterCurrency + "_" + timeInMinutes + ".csv";
    }

    public String getCounterCurrency() {
        return counterCurrency;
    }

    public double getSizeMultiplication() {
        return sizeMultiplication;
    }

    public double getLeverageFactor() {
        return leverageFactor;
    }

    public long getTimeInMinutes() {
        return timeInMinutes;
    }

    public String getTechnicalName() {
        return technicalName;
    }

    public Duration toJavaDuration() {
        return Duration.ofMinutes(getTimeInMinutes());
    }


    @Override
    public String toString() {
        return baseCurrency + counterCurrency + "_" + timeInMinutes;
    }
}
