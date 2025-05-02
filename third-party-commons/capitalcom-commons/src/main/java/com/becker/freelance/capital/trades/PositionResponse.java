package com.becker.freelance.capital.trades;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@Getter
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

}
