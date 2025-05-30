package com.becker.freelance.strategies.init;

import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PermutableStrategyInitParameter {

    private final List<StrategyInitParameter> strategyInitParameter;

    public PermutableStrategyInitParameter(List<StrategyInitParameter> strategyInitParameter) {
        this(strategyInitParameter, p -> true);
    }
    private final Predicate<Map<String, Decimal>> parameterValidation;

    public PermutableStrategyInitParameter(List<StrategyInitParameter> strategyInitParameter, Predicate<Map<String, Decimal>> parameterValidation) {
        this.strategyInitParameter = strategyInitParameter;
        this.parameterValidation = parameterValidation;
    }

    public PermutableStrategyInitParameter(Predicate<Map<String, Decimal>> parameterValidation, StrategyInitParameter... strategyInitParameter) {
        this(List.of(strategyInitParameter), parameterValidation);
    }

    public PermutableStrategyInitParameter(StrategyInitParameter... strategyInitParameter) {
        this(List.of(strategyInitParameter));
    }

    private static List<Map<String, Decimal>> internalPermute(List<Map<String, Decimal>> lastResult, List<StrategyInitParameter> strategyInitParameter) {
        if (strategyInitParameter.isEmpty()) {
            return lastResult;
        }

        StrategyInitParameter currParam = strategyInitParameter.remove(0);
        List<Map<String, Decimal>> result = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            for (Map<String, Decimal> res : lastResult) {
                Map<String, Decimal> copy = new HashMap<>(res);
                copy.put(currParam.getName(), param);
                result.add(copy);
            }
        }

        return internalPermute(result, strategyInitParameter);
    }

    public Map<String, Decimal> defaultValues() {
        Map<String, Decimal> defaultValues = new HashMap<>();
        for (StrategyInitParameter param : strategyInitParameter) {
            defaultValues.put(param.getName(), param.getDefaultValue());
        }
        return defaultValues;
    }

    private List<Map<String, Decimal>> validatePermutations(List<Map<String, Decimal>> permutated) {
        return permutated.stream().filter(parameterValidation).toList();
    }

    public List<Map<String, Decimal>> permutate() {
        if (strategyInitParameter.isEmpty()) {
            return new ArrayList<>();
        }

        List<StrategyInitParameter> workList = new ArrayList<>(strategyInitParameter);
        StrategyInitParameter currParam = workList.remove(0);
        List<Map<String, Decimal>> start = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            Map<String, Decimal> initial = new HashMap<>();
            initial.put(currParam.getName(), param);
            start.add(initial);
        }

        List<Map<String, Decimal>> permutated = internalPermute(start, workList);
        return validatePermutations(permutated);
    }
}

