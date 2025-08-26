package com.becker.freelance.bybit.trades;

class CreatePositionStopLimitDistanceRequest extends CreatePositionRequest {

    private Double stopDistance;
    private Double limitDistance;

    public CreatePositionStopLimitDistanceRequest(String epic, String direction, Double size, Double stopDistance, Double limitDistance) {
        super(epic, direction, size);
        this.stopDistance = stopDistance;
        this.limitDistance = limitDistance;
    }

    public Double getStopDistance() {
        return stopDistance;
    }

    public Double getLimitDistance() {
        return limitDistance;
    }
}
