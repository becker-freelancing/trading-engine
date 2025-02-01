package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.pair.Pair;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

class PairDeserializer extends JsonDeserializer<Pair> {
    @Override
    public Pair deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        TreeNode node = p.getCodec().readTree(p);
        String technicalName = node.get("technicalName").toString();
        return Pair.fromTechnicalName(technicalName);
    }
}
