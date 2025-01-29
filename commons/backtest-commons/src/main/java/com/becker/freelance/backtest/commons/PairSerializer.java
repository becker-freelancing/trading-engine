package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.pair.Pair;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

class PairSerializer extends JsonSerializer<Pair> {
    @Override
    public void serialize(Pair value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("technicalName", value.technicalName());
        gen.writeEndObject();
    }
}
