package com.becker.freelance.bybit.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class MarketDataResponse {

    private String topic;
    private List<MarketDataResponseData> data;

    public String getTopic() {
        return topic;
    }

    public List<MarketDataResponseData> getData() {
        return data;
    }
}
