package com.becker.freelance.strategies.validinitparameter;

import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.ParameterName;
import com.becker.freelance.strategies.creation.StrategyParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ValidStrategyInitParameters {

    private final List<StrategyInitParameter> strategyInitParameter;
    private final Predicate<StrategyParameter> parameterValidation;


    public ValidStrategyInitParameters(Predicate<StrategyParameter> parameterValidation, List<StrategyInitParameter> strategyInitParameter) {
        this.strategyInitParameter = strategyInitParameter;
        this.parameterValidation = parameterValidation;
    }

    public ValidStrategyInitParameters(List<StrategyInitParameter> strategyInitParameter) {
        this(p -> true, strategyInitParameter);
    }

    public ValidStrategyInitParameters(Predicate<StrategyParameter> parameterValidation, StrategyInitParameter... strategyInitParameter) {
        this(parameterValidation, List.of(strategyInitParameter));
    }

    public ValidStrategyInitParameters(StrategyInitParameter... strategyInitParameter) {
        this(List.of(strategyInitParameter));
    }

    private static List<StrategyParameter> internalPermute(List<StrategyParameter> lastResult, List<StrategyInitParameter> strategyInitParameter) {
        if (strategyInitParameter.isEmpty()) {
            return lastResult;
        }

        StrategyInitParameter currParam = strategyInitParameter.remove(0);
        List<StrategyParameter> result = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            for (StrategyParameter strategyParameter : lastResult) {
                StrategyParameter copy = new StrategyParameter(strategyParameter);
                copy.addParameter(currParam.getName(), param);
                result.add(copy);
            }
        }

        return internalPermute(result, strategyInitParameter);
    }

    private List<StrategyParameter> validatePermutations(List<StrategyParameter> permutated) {
        return permutated.stream().filter(parameterValidation).toList();
    }

    public List<StrategyParameter> permutate() {
        if (strategyInitParameter.isEmpty()) {
            return new ArrayList<>();
        }

        List<StrategyInitParameter> workList = new ArrayList<>(strategyInitParameter);
        StrategyInitParameter currParam = workList.remove(0);
        List<StrategyParameter> start = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            Map<ParameterName, Decimal> initial = new HashMap<>();
            initial.put(currParam.getName(), param);
            start.add(new StrategyParameter(initial));
        }

        List<StrategyParameter> permutated = internalPermute(start, workList);
        return validatePermutations(permutated);
    }


    public StrategyParameter defaultValues() {
        StrategyParameter parameter = new StrategyParameter(Map.of());
        for (StrategyInitParameter param : strategyInitParameter) {
            parameter.addParameter(param.getName(), param.getDefaultValue());
        }
        return parameter;
    }
}

