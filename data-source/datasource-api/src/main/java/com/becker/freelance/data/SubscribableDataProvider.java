package com.becker.freelance.data;

public abstract class SubscribableDataProvider implements Synchronizeable {

    public abstract void addSubscriber(DataSubscriber subscriber);
}
