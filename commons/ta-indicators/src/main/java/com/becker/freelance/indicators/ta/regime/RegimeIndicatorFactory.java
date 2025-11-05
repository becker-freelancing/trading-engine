package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.ta4j.core.BarSeries;
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

    public Indicator<TradeableMarketRegime> marketRegimeIndicatorForStrategy(Pair pair, Indicator<Num> closePrice) {
        if (isRegimeDetectionDisabled()){
            return getDisabledIndicator(closePrice);
        }
        if (divideRegimesIntoQuantiles(pair)) {
            return quantileMarketRegimeIndicator(pair, closePrice);
        }
        return marketRegimeIndicatorFromConfigFile(pair, closePrice);
    }

    public Indicator<TradeableMarketRegime> marketRegimeIndicatorFromConfigFile(Pair pair, Indicator<Num> closePrice) {
        if (isRegimeDetectionDisabled()){
            throw new IllegalStateException("Market Regime Detection is disabled");
        }
        JSONObject configForPair = loadConfigForPair(pair);
        JSONObject regimeDetectorConfig = configForPair.getJSONObject("regimeDetector");
        return new MarketRegimeIndicator(closePrice,
                regimeDetectorConfig.getDouble("volaSplitThreshold"),
                regimeDetectorConfig.getDouble("trendReversalSlopeThreshold"),
                regimeDetectorConfig.getInt("trendSlopeShift")
        );
    }

    private Indicator<DurationMarketRegime> durationMarketRegimeIndicator(Indicator<TradeableMarketRegime> marketRegimeIndicator) {

        return new DurationMarketRegimeIndicator(marketRegimeIndicator);
    }

    public Indicator<TradeableMarketRegime> quantileMarketRegimeIndicator(Pair pair, Indicator<Num> closePrice) {
        Indicator<TradeableMarketRegime> regimeIndicator = marketRegimeIndicatorFromConfigFile(pair, closePrice);
        Indicator<DurationMarketRegime> durationMarketRegimeIndicator = durationMarketRegimeIndicator(regimeIndicator);
        JSONObject configForPair = loadConfigForPair(pair).getJSONObject("quantileRegimeDetector");
        Map<MarketRegime, List<Double>> quantiles = Arrays.stream(MarketRegime.values())
                .map(regime -> new AbstractMap.SimpleEntry<>(regime, configForPair.getJSONArray(regime.toString())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> toList(entry.getValue())
                ));
        return new QuantilesMarketRegimeIndicator(durationMarketRegimeIndicator, quantiles);
    }

    private Indicator<TradeableMarketRegime> getDisabledIndicator(Indicator<Num> closePrice){
        return getDisabledIndicator(closePrice.getBarSeries()) ;
    }

    private Indicator<TradeableMarketRegime> getDisabledIndicator(BarSeries barSeries) {
        return new DisabledMarketRegimeIndicator(barSeries);
    }

    private boolean isRegimeDetectionDisabled() {
        return loadConfigFile().getBoolean("disabled");
    }

    private boolean divideRegimesIntoQuantiles(Pair pair) {
        return loadConfigForPair(pair).getBoolean("divideRegimesIntoQuantiles");
    }

    private List<Double> toList(JSONArray array) {
        return IntStream.range(0, array.length())
                .mapToObj(array::getDouble)
                .toList();
    }

    private JSONObject loadConfigForPair(Pair pair) {
        JSONArray configFile = loadConfigFile().getJSONArray("configuration");
        return IntStream.range(0, configFile.length())
                .mapToObj(configFile::getJSONObject)
                .filter(config -> pair.technicalName().equals(config.getString("pair")))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find config for pair with name " + pair.technicalName()));
    }

    private JSONObject loadConfigFile() {
        String fileName = "regime-config.json";
        InputStream fileInput = RegimeIndicatorFactory.class.getClassLoader().getResourceAsStream(fileName);
        try {
            return new JSONObject(new String(fileInput.readAllBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not load regime config file with name " + fileName);
        }
    }
}
