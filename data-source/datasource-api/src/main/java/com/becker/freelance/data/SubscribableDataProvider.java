package com.becker.freelance.data;

import com.becker.freelance.commons.timeseries.TimeSeries;

public abstract class SubscribableDataProvider implements Synchronizeable {

    public abstract TimeSeries getCurrentTimeSeries();

    public abstract void addSubscriber(DataSubscriber subscriber);
}
