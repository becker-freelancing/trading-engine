package com.becker.freelance.bybit.trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class CreatePositionResponse {

    private String dealReference;

    public String getDealReference() {
        return dealReference;
    }
}
