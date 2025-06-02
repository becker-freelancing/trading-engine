package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TradeableMarketRegimeDeserializer extends JsonDeserializer<TradeableQuantilMarketRegime> {
    @Override
    public TradeableQuantilMarketRegime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode node = p.getCodec().readTree(p);
        String regimeName = node.get("regimeName").toString();
        int regimeId = Integer.parseInt(node.get("regimeId").toString());

        return new UnmodifiableMarketRegime(regimeName, regimeId);
    }

    private record UnmodifiableMarketRegime(String name, int id) implements TradeableQuantilMarketRegime {

    }
}
