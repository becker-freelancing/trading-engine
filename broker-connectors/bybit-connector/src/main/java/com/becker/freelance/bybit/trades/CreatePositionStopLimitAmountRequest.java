package com.becker.freelance.bybit.trades;

import lombok.Getter;

@Getter
class CreatePositionStopLimitAmountRequest extends CreatePositionRequest {

    private Double stopAmount;
    private Double limitAmount;

    public CreatePositionStopLimitAmountRequest(String epic, String direction, Double size, Double stopAmount, Double limitAmount) {
        super(epic, direction, size);
        this.stopAmount = stopAmount;
        this.limitAmount = limitAmount;
    }


}
