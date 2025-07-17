package com.becker.freelance.strategies.validinitparameter;

import com.becker.freelance.strategies.creation.StrategyCreationParameter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;

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

    public List<StrategyCreationParameter> asShuffeledList() {
        Collections.shuffle(strategyCreationParameters, new Random(42));
        return strategyCreationParameters;
    }

    public Stream<StrategyCreationParameter> evenlyDistributed(Integer limit) {
        double totalSize = size();
        if (totalSize < limit) {
            return strategyCreationParameters.stream();
        }
        List<StrategyCreationParameter> result = new ArrayList<>();
        for (double d = 0.; d < totalSize; d += totalSize / limit) {
            int idx = (int) Math.round(d);
            if (idx >= strategyCreationParameters.size()){
                result.add(strategyCreationParameters.get(strategyCreationParameters.size() - 1));
                break;
            }
            result.add(strategyCreationParameters.get(idx));
        }

        return result.stream();
    }

    public int size() {
        return strategyCreationParameters.size();
    }
}
