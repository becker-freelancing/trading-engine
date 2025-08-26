package com.becker.freelance.capital.trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionItem {
    private PositionResponse position;
    private MarketData market;

    public PositionResponse getPosition() {
        return position;
    }

    public void setPosition(PositionResponse position) {
        this.position = position;
    }

    public MarketData getMarket() {
        return market;
    }

    public void setMarket(MarketData market) {
        this.market = market;
    }
}
