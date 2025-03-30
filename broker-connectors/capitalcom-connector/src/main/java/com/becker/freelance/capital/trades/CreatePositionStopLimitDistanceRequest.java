package com.becker.freelance.capital.trades;

import lombok.Getter;

@Getter
class CreatePositionStopLimitDistanceRequest extends CreatePositionRequest {

    private Double stopDistance;
    private Double limitDistance;

    public CreatePositionStopLimitDistanceRequest(String epic, String direction, Double size, Double stopDistance, Double limitDistance) {
        super(epic, direction, size);
        this.stopDistance = stopDistance;
        this.limitDistance = limitDistance;
    }
}
