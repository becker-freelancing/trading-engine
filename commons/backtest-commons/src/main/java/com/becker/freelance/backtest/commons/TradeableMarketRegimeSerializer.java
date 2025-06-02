package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class TradeableMarketRegimeSerializer extends JsonSerializer<TradeableQuantilMarketRegime> {
    @Override
    public void serialize(TradeableQuantilMarketRegime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("regimeName", value.name());
        gen.writeNumberField("regimeId", value.id());
        gen.writeEndObject();
    }
}
