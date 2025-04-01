package com.becker.freelance.bybit.trades;

import lombok.Getter;

@Getter
abstract class CreatePositionRequest {

    private String epic;
    private String direction;
    private Double size;

    public CreatePositionRequest(String epic, String direction, Double size) {
        this.epic = epic;
        this.direction = direction;
        this.size = size;
    }
}
