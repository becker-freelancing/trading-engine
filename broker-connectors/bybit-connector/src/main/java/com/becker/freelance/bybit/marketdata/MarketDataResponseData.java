package com.becker.freelance.bybit.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
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
}
