package com.becker.freelance.strategies.validinitparameter;

import com.becker.freelance.trading.external.services.strategies.StrategyCreationParameter;
import com.becker.freelance.trading.external.services.strategies.StrategyInitParameter;

import java.util.List;
import java.util.function.Predicate;

public class ValidStrategyInitParameters {

    private final List<StrategyInitParameter> strategyInitParameter;
    private final Predicate<StrategyCreationParameter> parameterValidation;


    public ValidStrategyInitParameters(Predicate<StrategyCreationParameter> parameterValidation, List<StrategyInitParameter> strategyInitParameter) {
        this.strategyInitParameter = strategyInitParameter;
        this.parameterValidation = parameterValidation;
    }

    public ValidStrategyInitParameters(List<StrategyInitParameter> strategyInitParameter) {
        this(p -> true, strategyInitParameter);
    }

    public ValidStrategyInitParameters(Predicate<StrategyCreationParameter> parameterValidation, StrategyInitParameter... strategyInitParameter) {
        this(parameterValidation, List.of(strategyInitParameter));
    }

    public ValidStrategyInitParameters(StrategyInitParameter... strategyInitParameter) {
        this(List.of(strategyInitParameter));
    }

    public int unfilteredPermutationSize() {
        int size = 1;
        for (StrategyInitParameter initParameter : strategyInitParameter) {
            size *= initParameter.getSize();
        }

        return size;
    }


    public List<StrategyInitParameter> getStrategyInitParameter() {
        return strategyInitParameter;
    }

    public Predicate<StrategyCreationParameter> getParameterValidation() {
        return parameterValidation;
    }
}

