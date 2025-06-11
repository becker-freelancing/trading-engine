package com.becker.freelance.commons.order;

import com.becker.freelance.math.Decimal;

public interface LazyOrder extends Order {

    public Decimal getNearestExecutionPrice();

    void setExecutionLevel(Decimal level);

    @Override
    LazyOrder clone();
}
