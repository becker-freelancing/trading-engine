package com.becker.freelance.commons.order;

import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface LazyOrder extends Order {

    public Decimal getNearestExecutionPrice();

    void setExecutionLevel(Decimal level, LocalDateTime currentTime);

    @Override
    LazyOrder clone();
}
