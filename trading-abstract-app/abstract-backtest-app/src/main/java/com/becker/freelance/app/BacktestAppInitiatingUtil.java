package com.becker.freelance.app;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.data.DataProvider;
import com.becker.freelance.strategies.BaseStrategy;

import java.io.IOException;
import java.util.List;

class BacktestAppInitiatingUtil {
    
    private final PropertyAsker propertyAsker;
    
    public BacktestAppInitiatingUtil(){
        propertyAsker = new PropertyAsker();
    }


    public BaseStrategy askStrategy() {
        BaseStrategy strategy = propertyAsker.askProperty(BaseStrategy.loadAll(), BaseStrategy::getName, "Strategie");
        return strategy;
    }




    public Integer askNumberOfThreads() {
        Integer numThreads = propertyAsker.askProperty(List.of(1, 20, 40, 80), i -> Integer.toString(i), "Anzahl an Threads");
        return numThreads;
    }

    public Pair askPair() {
        Pair pair = propertyAsker.askProperty(Pair.allPairs(), Pair::technicalName, "Pair");
        return pair;
    }

    public AppMode askAppMode() {
        AppMode appMode = propertyAsker.askProperty(AppMode.findAll(), AppMode::getDescription, "AppMode");
        return appMode;
    }

}
