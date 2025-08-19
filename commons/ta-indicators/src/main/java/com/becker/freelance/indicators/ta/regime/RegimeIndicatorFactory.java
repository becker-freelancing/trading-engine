package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.Num;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegimeIndicatorFactory {

    public Indicator<? extends TradeableMarketRegime> marketRegimeIndicatorForStrategy(Pair pair, Indicator<Num> closePrice) {
        Indicator<MarketRegime> marketRegimeIndicator = marketRegimeIndicatorFromConfigFile(pair, closePrice);
        if (divideRegimesIntoQuantiles(pair)) {
            Indicator<DurationMarketRegime> durationMarketRegimeIndicator = durationMarketRegimeIndicator(marketRegimeIndicator);
            return quantileMarketRegimeIndicator(pair, durationMarketRegimeIndicator);
        }
        return marketRegimeIndicator;
    }

    private boolean divideRegimesIntoQuantiles(Pair pair) {
        return loadConfigForPair(pair).getBoolean("divideRegimesIntoQuantiles");
    }

    public Indicator<MarketRegime> marketRegimeIndicatorFromConfigFile(Pair pair, Indicator<Num> closePrice) {
        JSONObject configForPair = loadConfigForPair(pair);
        JSONObject regimeDetectorConfig = configForPair.getJSONObject("regimeDetector");
        return new MarketRegimeIndicator(closePrice,
                regimeDetectorConfig.getDouble("volaSplitThreshold"),
                regimeDetectorConfig.getDouble("trendReversalSlopeThreshold"),
                regimeDetectorConfig.getInt("trendSlopeShift")
        );
    }

    public Indicator<DurationMarketRegime> durationMarketRegimeIndicator(Indicator<MarketRegime> marketRegimeIndicator) {
        return new DurationMarketRegimeIndicator(marketRegimeIndicator);
    }

    public Indicator<QuantileMarketRegime> quantileMarketRegimeIndicator(Pair pair, Indicator<DurationMarketRegime> durationMarketRegimeIndicator) {
        JSONObject configForPair = loadConfigForPair(pair).getJSONObject("quantileRegimeDetector");
        Map<MarketRegime, List<Double>> quantiles = Arrays.stream(MarketRegime.values())
                .map(regime -> new AbstractMap.SimpleEntry<>(regime, configForPair.getJSONArray(regime.toString())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> toList(entry.getValue())
                ));
        return new QuantilesMarketRegimeIndicator(durationMarketRegimeIndicator, quantiles);
    }

    private List<Double> toList(JSONArray array) {
        return IntStream.range(0, array.length())
                .mapToObj(array::getDouble)
                .toList();
    }

    private JSONObject loadConfigForPair(Pair pair) {
        JSONArray configFile = new JSONArray(loadConfigFile());
        return IntStream.range(0, configFile.length())
                .mapToObj(configFile::getJSONObject)
                .filter(config -> pair.technicalName().equals(config.getString("pair")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find config for pair with name " + pair.technicalName()));
    }

    private String loadConfigFile() {
        String fileName = "regime-config.json";
        InputStream fileInput = RegimeIndicatorFactory.class.getClassLoader().getResourceAsStream(fileName);
        try {
            return new String(fileInput.readAllBytes());
        } catch (IOException e) {
            throw new IllegalStateException("Could not load regime config file with name " + fileName);
        }
    }
}
