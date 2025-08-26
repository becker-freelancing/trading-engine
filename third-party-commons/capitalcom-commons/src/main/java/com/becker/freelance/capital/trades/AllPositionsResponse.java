package com.becker.freelance.capital.trades;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllPositionsResponse {

    private List<PositionItem> positions;

    public List<PositionItem> getPositions() {
        return positions;
    }
}
