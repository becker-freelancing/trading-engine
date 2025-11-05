package com.becker.freelance.data;

import com.becker.freelance.commons.calculation.PriceRequestor;

public abstract class SubscribableDataProvider implements Synchronizeable, PriceRequestor {

    public abstract void addSubscriber(DataSubscriber subscriber);
}
