package com.becker.freelance.strategies;

import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PermutableStrategyParameter {

    private static List<Map<String, Decimal>> internalPermute(List<Map<String, Decimal>> lastResult, List<StrategyParameter> strategyParameter) {
        if (strategyParameter.isEmpty()) {
            return lastResult;
        }

        StrategyParameter currParam = strategyParameter.remove(0);
        List<Map<String, Decimal>> result = new ArrayList<>();

        for (Decimal param = currParam.getMinValue(); param.isLessThanOrEqualTo(currParam.getMaxValue()); param = param.add(currParam.getStepSize())) {
            for (Map<String, Decimal> res : lastResult) {
                Map<String, Decimal> copy = new HashMap<>(res);
                copy.put(currParam.getName(), param);
                result.add(copy);
            }
        }

        return internalPermute(result, strategyParameter);
    }

    public static boolean allValidValidation(Map<String, Decimal> parameter) {
        return true;
    }

    private final List<StrategyParameter> strategyParameter;
    private final Predicate<Map<String, Decimal>> parameterValidation;

    public PermutableStrategyParameter(List<StrategyParameter> strategyParameter) {
        this(strategyParameter, p -> true);
    }

    public PermutableStrategyParameter(List<StrategyParameter> strategyParameter, Predicate<Map<String, Decimal>> parameterValidation) {
        this.strategyParameter = strategyParameter;
        this.parameterValidation = parameterValidation;
    }

    public PermutableStrategyParameter(Predicate<Map<String, Decimal>> parameterValidation, StrategyParameter... strategyParameter) {
        this(List.of(strategyParameter), parameterValidation);
    }


    public PermutableStrategyParameter(StrategyParameter... strategyParameter) {
        this(List.of(strategyParameter));
    }

    public Map<String, Decimal> defaultValues() {
        Map<String, Decimal> defaultValues = new HashMap<>();
        for (StrategyParameter param : strategyParameter) {
            defaultValues.put(param.getName(), param.getDefaultValue());
        }
        return defaultValues;
    }

    private List<Map<String, Decimal>> validatePermutations(List<Map<String, Decimal>> permutated) {
        return permutated.stream().filter(parameterValidation).toList();
    }

    public List<Map<String, Decimal>> permutate() {
        if (strategyParameter.isEmpty()) {
            return new ArrayList<>();
        }

        List<StrategyParameter> workList = new ArrayList<>(strategyParameter);
        StrategyParameter currParam = workList.remove(0);
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

