package com.becker.freelance.bybit.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
class MarketDataResponse {

    private String topic;
    private List<MarketDataResponseData> data;
}
