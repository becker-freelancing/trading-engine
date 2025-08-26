package com.becker.freelance.bybit.trades;

abstract class CreatePositionRequest {

    private String epic;
    private String direction;
    private Double size;

    public CreatePositionRequest(String epic, String direction, Double size) {
        this.epic = epic;
        this.direction = direction;
        this.size = size;
    }

    public String getEpic() {
        return epic;
    }

    public String getDirection() {
        return direction;
    }

    public Double getSize() {
        return size;
    }
}
