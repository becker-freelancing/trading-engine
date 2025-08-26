package com.becker.freelance.bybit.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class MarketDataResponseData {

    private long start;
    private long end;
    private String interval;
    private String open;
    private String close;
    private String high;
    private String low;
    private String volume;
    private Boolean confirm;

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getInterval() {
        return interval;
    }

    public String getOpen() {
        return open;
    }

    public String getClose() {
        return close;
    }

    public String getHigh() {
        return high;
    }

    public String getLow() {
        return low;
    }

    public String getVolume() {
        return volume;
    }

    public Boolean getConfirm() {
        return confirm;
    }
}
