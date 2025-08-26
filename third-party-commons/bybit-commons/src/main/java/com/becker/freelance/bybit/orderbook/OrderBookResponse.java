package com.becker.freelance.bybit.orderbook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderBookResponse {

    private String topic;
    private Long ts;
    private String type;
    private Data data;
    private Long cts;

    public String getTopic() {
        return topic;
    }

    public Long getTs() {
        return ts;
    }

    public String getType() {
        return type;
    }

    public Data getData() {
        return data;
    }

    public Long getCts() {
        return cts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data {
        private String s;
        private List<List<String>> b;
        private List<List<String>> a;
        private Long u;
        private Long seq;

        public String getS() {
            return s;
        }

        public List<List<String>> getB() {
            return b;
        }

        public List<List<String>> getA() {
            return a;
        }

        public Long getU() {
            return u;
        }

        public Long getSeq() {
            return seq;
        }
    }
}
