package com.becker.freelance.strategies.strategy;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;

import java.util.List;

public interface Initializable {

    public int initializationBarCount();

    public Pair getPair();

    public void processInitData(TimeSeries timeSeries);

    public List<Initializable> getTransitiveInitializables();
}
