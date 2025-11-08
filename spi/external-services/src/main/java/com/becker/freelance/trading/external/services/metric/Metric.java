package com.becker.freelance.trading.external.services.metric;

public interface Metric<VALUE> {

    public VALUE value();

    public String name();

    public String unit();
}
