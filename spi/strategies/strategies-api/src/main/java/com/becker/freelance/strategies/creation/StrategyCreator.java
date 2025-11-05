package com.becker.freelance.strategies.creation;

import com.becker.freelance.commons.service.ExtServiceLoader;
import com.becker.freelance.strategies.strategy.StrategyParameter;
import com.becker.freelance.strategies.strategy.TradingStrategy;
import com.becker.freelance.strategies.validinitparameter.ValidStrategyInitParameters;

import java.util.Comparator;
import java.util.List;

public interface StrategyCreator {

    public static List<StrategyCreator> findAll() {
        return ExtServiceLoader.loadMultiple(StrategyCreator.class)
                .sorted(Comparator.comparing(StrategyCreator::strategyName))
                .toList();
    }

    public String strategyName();

    public ValidStrategyInitParameters strategyParameters();

    public TradingStrategy build(StrategyParameter strategyParameter);

}
