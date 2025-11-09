package com.becker.freelance.trading.external.services.backtest.strategies;

import com.becker.freelance.trading.external.services.registry.ExternalService;
import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;
import com.becker.freelance.trading.external.services.strategies.StrategyInitParameter;

import java.util.List;
import java.util.function.Predicate;

public interface StrategyParamsSampler extends ExternalService {
    List<StrategyCreationParameter> sample(List<StrategyInitParameter> strategyInitParameter, Predicate<StrategyCreationParameter> parameterValidation);
}
