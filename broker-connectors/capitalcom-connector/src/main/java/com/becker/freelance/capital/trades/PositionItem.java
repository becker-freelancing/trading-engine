package com.becker.freelance.capital.trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PositionItem {
    private PositionResponse position;
    private MarketData market;
}
