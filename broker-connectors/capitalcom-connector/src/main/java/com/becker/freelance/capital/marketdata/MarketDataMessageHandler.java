package com.becker.freelance.capital.marketdata;

import com.becker.freelance.capital.util.PairConverter;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.MessageHandler;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Consumer;

class MarketDataMessageHandler implements MessageHandler.Whole<String> {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataMessageHandler.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final PairConverter PAIR_CONVERTER = new PairConverter();
    private static final String BID_PRICE_TYPE = "bid";
    private static final String ASK_PRICE_TYPE = "ask";
    private static final String OK_STATUS = "OK";

    private final Consumer<BidMarketData> bidConsumer;
    private final Consumer<AskMarketData> askConsumer;

    public MarketDataMessageHandler(Consumer<BidMarketData> bidConsumer, Consumer<AskMarketData> askConsumer) {
        this.bidConsumer = bidConsumer;
        this.askConsumer = askConsumer;
    }

    @Override
    public void onMessage(String s) {
        if (s.contains("correlationId")) {
            return;
        }
        OHLCMessage ohlcMessage;
        try {
            ohlcMessage = OBJECT_MAPPER.readValue(s, OHLCMessage.class);
        } catch (JsonProcessingException e) {
            logger.error("Could not convert Message {} to {}", s, OHLCMessage.class, e);
            return;
        }

        if (!OK_STATUS.equals(ohlcMessage.getStatus())) {
            logger.warn("Marketdata received with Status not OK: {}", ohlcMessage);
            return;
        }

        if (BID_PRICE_TYPE.equals(ohlcMessage.getPayload().getPriceType())) {
            consumeBidPrice(ohlcMessage);
        } else if (ASK_PRICE_TYPE.equals(ohlcMessage.getPayload().getPriceType())) {
            consumeAskPrice(ohlcMessage);
        }
    }

    private void consumeAskPrice(OHLCMessage ohlcMessage) {
        Payload payload = ohlcMessage.getPayload();
        Pair pair = PAIR_CONVERTER.convert(payload.getEpic(), payload.getResolution());
        LocalDateTime closeTime = map(payload.getT());
        AskMarketData askMarketData = new AskMarketData(pair, closeTime, new Decimal(payload.getO()), new Decimal(payload.getH()), new Decimal(payload.getL()), new Decimal(payload.getC()));
        askConsumer.accept(askMarketData);
    }

    private LocalDateTime map(long time) {
        return Instant.ofEpochMilli(time)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private void consumeBidPrice(OHLCMessage ohlcMessage) {
        Payload payload = ohlcMessage.getPayload();
        Pair pair = PAIR_CONVERTER.convert(payload.getEpic(), payload.getResolution());
        LocalDateTime closeTime = map(payload.getT());
        BidMarketData bidMarketData = new BidMarketData(pair, closeTime, new Decimal(payload.getO()), new Decimal(payload.getH()), new Decimal(payload.getL()), new Decimal(payload.getC()));
        bidConsumer.accept(bidMarketData);
    }

    @Getter
    private static class OHLCMessage {
        private String status;
        private String destination;
        private Payload payload;

        @Override
        public String toString() {
            return "OHLCMessage{" +
                    "status='" + status + '\'' +
                    ", destination='" + destination + '\'' +
                    ", payload=" + payload +
                    '}';
        }
    }

    @Getter
    private static class Payload {
        private String resolution;
        private String epic;
        private String type;
        private String priceType;
        private long t;
        private double h;
        private double l;
        private double o;
        private double c;

        @Override
        public String toString() {
            return "Payload{" +
                    "resolution='" + resolution + '\'' +
                    ", epic='" + epic + '\'' +
                    ", type='" + type + '\'' +
                    ", priceType='" + priceType + '\'' +
                    ", t=" + t +
                    ", h=" + h +
                    ", l=" + l +
                    ", o=" + o +
                    ", c=" + c +
                    '}';
        }
    }
}
