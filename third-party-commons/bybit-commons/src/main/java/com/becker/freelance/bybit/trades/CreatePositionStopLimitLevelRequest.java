package com.becker.freelance.bybit.trades;

import lombok.Getter;

@Getter
class CreatePositionStopLimitLevelRequest extends CreatePositionRequest {

    private Double stopLevel;
    private Double limitLevel;

    public CreatePositionStopLimitLevelRequest(String epic, String direction, Double size, Double stopLevel, Double limitLevel) {
        super(epic, direction, size);
        this.stopLevel = stopLevel;
        this.limitLevel = limitLevel;
    }
}
