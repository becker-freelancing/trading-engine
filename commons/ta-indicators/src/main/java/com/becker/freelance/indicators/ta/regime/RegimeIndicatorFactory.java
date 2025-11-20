package com.becker.freelance.indicators.ta.regime;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableMarketRegime;
import com.becker.freelance.indicators.ta.temporal.indicator.TemporalIndicator;
import com.becker.freelance.indicators.ta.temporal.series.TemporalBarSeries;
import com.becker.freelance.math.Decimal;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RegimeIndicatorFactory {

    public TemporalIndicator<TradeableMarketRegime> marketRegimeIndicatorForStrategy(Pair pair, TemporalIndicator<Decimal> closePrice) {
        if (isRegimeDetectionDisabled()){
            return getDisabledIndicator(closePrice.getBarSeries());
        }
        if (divideRegimesIntoQuantiles(pair)) {
            return quantileMarketRegimeIndicator(pair, closePrice);
        }
        return marketRegimeIndicatorFromConfigFile(pair, closePrice);
    }

    public TemporalIndicator<TradeableMarketRegime> marketRegimeIndicatorFromConfigFile(Pair pair, TemporalIndicator<Decimal> closePrice) {
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

    private TemporalIndicator<DurationMarketRegime> durationMarketRegimeIndicator(TemporalIndicator<TradeableMarketRegime> marketRegimeIndicator) {

        return new DurationMarketRegimeIndicator(marketRegimeIndicator);
    }

    public TemporalIndicator<TradeableMarketRegime> quantileMarketRegimeIndicator(Pair pair, TemporalIndicator<Decimal> closePrice) {
        TemporalIndicator<TradeableMarketRegime> regimeIndicator = marketRegimeIndicatorFromConfigFile(pair, closePrice);
        TemporalIndicator<DurationMarketRegime> durationMarketRegimeIndicator = durationMarketRegimeIndicator(regimeIndicator);
        JSONObject configForPair = loadConfigForPair(pair).getJSONObject("quantileRegimeDetector");
        Map<MarketRegime, List<Double>> quantiles = Arrays.stream(MarketRegime.values())
                .map(regime -> new AbstractMap.SimpleEntry<>(regime, configForPair.getJSONArray(regime.toString())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> toList(entry.getValue())
                ));
        return new QuantilesMarketRegimeIndicator(durationMarketRegimeIndicator, quantiles);
    }

    private TemporalIndicator<TradeableMarketRegime> getDisabledIndicator(TemporalBarSeries barSeries) {
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
