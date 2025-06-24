package com.becker.freelance.trading.abstractapp.commons.strategyconfig;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.indicators.ta.regime.QuantileMarketRegime;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.*;
import com.becker.freelance.strategies.strategy.DefaultStrategyParameter;
import com.becker.freelance.strategies.strategy.StrategyParameter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StrategyFileConfigurator {

    //DUPLICATE
    public Stream<RegimeStrategyCreator> withConfigFile(StrategyCreator strategyCreator, Pair pair) {
        Optional<List<JSONObject>> configs = getConfigsForStrategy(strategyCreator.strategyName(), pair);
        return configs.flatMap(config -> map(config, strategyCreator, pair))
                .orElse(Stream.of(new RegimeStrategyCreator(strategyCreator,
                        QuantileMarketRegime.all(),
                        100,
                        pair,
                        new DefaultStrategyParameter(strategyCreator.strategyParameters().defaultValues(), pair, QuantileMarketRegime.all()))));
    }

    private Optional<Stream<RegimeStrategyCreator>> map(List<JSONObject> configs, StrategyCreator strategyCreator, Pair pair) {
        if (configs.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(configs.stream()
                .map(config -> map(config, strategyCreator, pair)));
    }

    private RegimeStrategyCreator map(JSONObject config, StrategyCreator strategyCreator, Pair pair) {
        int priority = config.getInt("priority");
        Set<QuantileMarketRegime> regimes = map(config.getJSONArray("regimes"));
        StrategyParameter parameters = map(config.getJSONObject("parameters"), pair, regimes);

        return new RegimeStrategyCreator(
                strategyCreator,
                regimes,
                priority,
                pair,
                parameters
        );
    }

    private StrategyParameter map(JSONObject parameters, Pair pair, Set<QuantileMarketRegime> regimes) {
        StrategyCreationParameter strategyCreationParameter = new DefaultStrategyCreationParameter(
                parameters.toMap().entrySet().stream()
                        .collect(Collectors.toMap(
                                entry -> new StringParameterName(entry.getKey()),
                                entry -> toDecimal(entry.getValue())))
        );
        return new DefaultStrategyParameter(
                strategyCreationParameter,
                pair,
                regimes
        );
    }

    private Decimal toDecimal(Object value) {
        if (value instanceof Double doubleValue) {
            return Decimal.valueOf(doubleValue);
        }
        if (value instanceof Integer integerValue) {
            return Decimal.valueOf(integerValue);
        }
        return new Decimal(String.valueOf(value));
    }

    private Set<QuantileMarketRegime> map(JSONArray regimes) {
        if (regimes.isEmpty()) {
            return Set.of();
        }

        return IntStream.range(0, regimes.length())
                .mapToObj(regimes::getString)
                .map(QuantileMarketRegime::valueOf)
                .collect(Collectors.toSet());

    }

    private Optional<List<JSONObject>> getConfigsForStrategy(String strategyName, Pair pair) {
        InputStream resource = StrategyFileConfigurator.class.getClassLoader().getResourceAsStream("strategy-config.json");
        if (resource == null) {
            return Optional.empty();
        }

        JSONArray array;
        try {
            array = new JSONArray(new String(resource.readAllBytes()));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config file", e);
        }
        return Optional.of(IntStream.range(0, array.length())
                .mapToObj(array::getJSONObject)
                .filter(obj -> strategyName.equals(obj.getString("strategyName")))
                .filter(obj -> pair.shortName().equals(obj.getString("pair")))
                .toList());
    }
}
