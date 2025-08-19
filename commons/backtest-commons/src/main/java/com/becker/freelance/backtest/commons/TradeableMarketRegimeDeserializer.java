package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Optional;

public class TradeableMarketRegimeDeserializer extends JsonDeserializer<TradeableMarketRegime> {
    @Override
    public TradeableMarketRegime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        TreeNode node = p.getCodec().readTree(p);
        String regimeName = node.get("regimeName").toString();
        int regimeId = Integer.parseInt(node.get("regimeId").toString());
        Optional<TreeNode> considersRegimeDurationNode = Optional.ofNullable(node.get("considersRegimeDuration"));
        boolean considersRegimeDuration = Boolean.parseBoolean(considersRegimeDurationNode.map(TreeNode::toString).orElse("false"));

        return new UnmodifiableMarketRegime(regimeName, regimeId, considersRegimeDuration);
    }

    private record UnmodifiableMarketRegime(String name, int id,
                                            boolean considersRegimeDuration) implements TradeableMarketRegime {

    }
}
