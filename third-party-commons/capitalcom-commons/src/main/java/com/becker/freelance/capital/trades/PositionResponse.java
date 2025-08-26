package com.becker.freelance.capital.trades;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionResponse {
    private Double contractSize;
    private String createdDate;
    private String createdDateUTC;
    private String dealId;
    private String dealReference;
    private String workingOrderId;
    private Double size;
    private Double leverage;
    private Double upl;
    private String direction;
    private Double level;
    private String currency;
    private Boolean guaranteedStop;

    public Double getContractSize() {
        return contractSize;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getCreatedDateUTC() {
        return createdDateUTC;
    }

    public String getDealId() {
        return dealId;
    }

    public String getDealReference() {
        return dealReference;
    }

    public String getWorkingOrderId() {
        return workingOrderId;
    }

    public Double getSize() {
        return size;
    }

    public Double getLeverage() {
        return leverage;
    }

    public Double getUpl() {
        return upl;
    }

    public String getDirection() {
        return direction;
    }

    public Double getLevel() {
        return level;
    }

    public String getCurrency() {
        return currency;
    }

    public Boolean getGuaranteedStop() {
        return guaranteedStop;
    }
}
