package com.becker.freelance.strategies.validinitparameter;

import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class PermutatedStrategyCreationParameter {

    private List<StrategyCreationParameter> strategyCreationParameters;

    public PermutatedStrategyCreationParameter() {
        this(new ArrayList<>());
    }

    public PermutatedStrategyCreationParameter(List<StrategyCreationParameter> strategyCreationParameters) {
        this.strategyCreationParameters = new ArrayList<>(strategyCreationParameters);
    }

    public PermutatedStrategyCreationParameter filter(Predicate<StrategyCreationParameter> predicate) {
        strategyCreationParameters = new ArrayList<>(strategyCreationParameters.stream().filter(predicate).toList());
        return this;
    }

    public List<StrategyCreationParameter> asSearchList() {
        Collections.shuffle(strategyCreationParameters, new Random(42));
        return strategyCreationParameters;
    }

    public int size() {
        return strategyCreationParameters.size();
    }
}
