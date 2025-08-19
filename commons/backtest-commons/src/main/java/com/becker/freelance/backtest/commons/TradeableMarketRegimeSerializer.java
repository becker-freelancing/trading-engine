package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TradeableMarketRegimeSerializer extends JsonSerializer<TradeableMarketRegime> {
    @Override
    public void serialize(TradeableMarketRegime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("regimeName", value.name());
        gen.writeNumberField("regimeId", value.id());
        gen.writeBooleanField("considersRegimeDuration", value.considersRegimeDuration());
        gen.writeEndObject();
    }
}
