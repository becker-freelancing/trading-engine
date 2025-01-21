package com.becker.freelance.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class PermutableStrategyParameter {

    private static List<Map<String, Double>> internalPermute(List<Map<String, Double>> lastResult, List<StrategyParameter> strategyParameter) {
        if (strategyParameter.isEmpty()) {
            return lastResult;
        }

        StrategyParameter currParam = strategyParameter.remove(0);
        List<Map<String, Double>> result = new ArrayList<>();

        for (double param = currParam.getMinValue(); param <= currParam.getMaxValue(); param += currParam.getStepSize()) {
            for (Map<String, Double> res : lastResult) {
                Map<String, Double> copy = new HashMap<>(res);
                copy.put(currParam.getName(), param);
                result.add(copy);
            }
        }

        return internalPermute(result, strategyParameter);
    }

    public static boolean allValidValidation(Map<String, Double> parameter) {
        return true;
    }

    private List<StrategyParameter> strategyParameter;
    private Predicate<Map<String, Double>> parameterValidation;

    public PermutableStrategyParameter(List<StrategyParameter> strategyParameter) {
        this(strategyParameter, p -> true);
    }

    public PermutableStrategyParameter(List<StrategyParameter> strategyParameter, Predicate<Map<String, Double>> parameterValidation) {
        this.strategyParameter = strategyParameter;
        this.parameterValidation = parameterValidation;
    }

    public PermutableStrategyParameter(Predicate<Map<String, Double>> parameterValidation, StrategyParameter... strategyParameter) {
        this(List.of(strategyParameter), parameterValidation);
    }


    public PermutableStrategyParameter(StrategyParameter... strategyParameter) {
        this(List.of(strategyParameter));
    }

    public Map<String, Double> defaultValues() {
        Map<String, Double> defaultValues = new HashMap<>();
        for (StrategyParameter param : strategyParameter) {
            defaultValues.put(param.getName(), param.getDefaultValue());
        }
        return defaultValues;
    }

    private List<Map<String, Double>> validatePermutations(List<Map<String, Double>> permutated) {
        return permutated.stream().filter(parameterValidation).toList();
    }

    public List<Map<String, Double>> permutate() {
        if (strategyParameter.isEmpty()) {
            return new ArrayList<>();
        }

        List<StrategyParameter> workList = new ArrayList<>(strategyParameter);
        StrategyParameter currParam = workList.remove(0);
        List<Map<String, Double>> start = new ArrayList<>();

        for (double param = currParam.getMinValue(); param <= currParam.getMaxValue(); param += currParam.getStepSize()) {
            Map<String, Double> initial = new HashMap<>();
            initial.put(currParam.getName(), param);
            start.add(initial);
        }

        List<Map<String, Double>> permutated = internalPermute(start, workList);
        return validatePermutations(permutated);
    }
}

