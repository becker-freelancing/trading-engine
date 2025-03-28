package com.becker.freelance.capital.ws;

public interface OnMarketDataCallback {

    public void onMarketData(OHLCBar quote) throws Exception;
}
