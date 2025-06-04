package com.becker.freelance.strategies.validinitparameter;

import com.becker.freelance.math.Decimal;
import com.becker.freelance.strategies.creation.DefaultStrategyCreationParameter;
import com.becker.freelance.strategies.creation.ParameterName;
import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static List<StrategyCreationParameter> internalPermute(List<StrategyCreationParameter> lastResult, List<StrategyInitParameter> strategyInitParameter) {
        if (strategyInitParameter.isEmpty()) {
            return lastResult;
        }

        StrategyInitParameter currParam = strategyInitParameter.remove(0);
        List<StrategyCreationParameter> result = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            for (StrategyCreationParameter strategyCreationParameter : lastResult) {
                StrategyCreationParameter copy = strategyCreationParameter.clone();
                copy.addParameter(currParam.getName(), param);
                result.add(copy);
            }
        }

        return internalPermute(result, strategyInitParameter);
    }

    private List<StrategyCreationParameter> validatePermutations(List<StrategyCreationParameter> permutated) {
        return permutated.stream().filter(parameterValidation).toList();
    }

    public List<StrategyCreationParameter> permutate() {
        if (strategyInitParameter.isEmpty()) {
            return new ArrayList<>();
        }

        List<StrategyInitParameter> workList = new ArrayList<>(strategyInitParameter);
        StrategyInitParameter currParam = workList.remove(0);
        List<StrategyCreationParameter> start = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            Map<ParameterName, Decimal> initial = new HashMap<>();
            initial.put(currParam.getName(), param);
            start.add(new DefaultStrategyCreationParameter(initial));
        }

        List<StrategyCreationParameter> permutated = internalPermute(start, workList);
        return validatePermutations(permutated);
    }


    public DefaultStrategyCreationParameter defaultValues() {
        DefaultStrategyCreationParameter parameter = new DefaultStrategyCreationParameter(Map.of());
        for (StrategyInitParameter param : strategyInitParameter) {
            parameter.addParameter(param.getName(), param.getDefaultValue());
        }
        return parameter;
    }
}

