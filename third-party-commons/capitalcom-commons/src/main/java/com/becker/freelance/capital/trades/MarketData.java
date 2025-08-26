package com.becker.freelance.capital.trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketData {
    private String instrumentName;
    private String expiry;
    private String epic;
    private InstrumentType instrumentType;
    private Float lotSize;
    private Float high;
    private Float low;
    private Float percentageChange;
    private Float netChange;
    private Float bid;
    private Float offer;
    private String updateTime;
    private Integer delayTime;
    private Boolean streamingPricesAvailable;
    private MarketStatus marketStatus;
    private Integer scalingFactor;

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getEpic() {
        return epic;
    }

    public void setEpic(String epic) {
        this.epic = epic;
    }

    public InstrumentType getInstrumentType() {
        return instrumentType;
    }

    public void setInstrumentType(InstrumentType instrumentType) {
        this.instrumentType = instrumentType;
    }

    public Float getLotSize() {
        return lotSize;
    }

    public void setLotSize(Float lotSize) {
        this.lotSize = lotSize;
    }

    public Float getHigh() {
        return high;
    }

    public void setHigh(Float high) {
        this.high = high;
    }

    public Float getLow() {
        return low;
    }

    public void setLow(Float low) {
        this.low = low;
    }

    public Float getPercentageChange() {
        return percentageChange;
    }

    public void setPercentageChange(Float percentageChange) {
        this.percentageChange = percentageChange;
    }

    public Float getNetChange() {
        return netChange;
    }

    public void setNetChange(Float netChange) {
        this.netChange = netChange;
    }

    public Float getBid() {
        return bid;
    }

    public void setBid(Float bid) {
        this.bid = bid;
    }

    public Float getOffer() {
        return offer;
    }

    public void setOffer(Float offer) {
        this.offer = offer;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(Integer delayTime) {
        this.delayTime = delayTime;
    }

    public Boolean getStreamingPricesAvailable() {
        return streamingPricesAvailable;
    }

    public void setStreamingPricesAvailable(Boolean streamingPricesAvailable) {
        this.streamingPricesAvailable = streamingPricesAvailable;
    }

    public MarketStatus getMarketStatus() {
        return marketStatus;
    }

    public void setMarketStatus(MarketStatus marketStatus) {
        this.marketStatus = marketStatus;
    }

    public Integer getScalingFactor() {
        return scalingFactor;
    }

    public void setScalingFactor(Integer scalingFactor) {
        this.scalingFactor = scalingFactor;
    }
}
