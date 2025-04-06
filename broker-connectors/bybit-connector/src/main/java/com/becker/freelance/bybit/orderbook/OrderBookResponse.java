package com.becker.freelance.bybit.orderbook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class OrderBookResponse {

    private String topic;
    private Long ts;
    private String type;
    private Data data;
    private Long cts;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    public static class Data {
        private String s;
        private List<List<String>> b;
        private List<List<String>> a;
        private Long u;
        private Long seq;
    }
}
