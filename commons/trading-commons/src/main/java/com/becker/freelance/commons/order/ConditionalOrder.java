package com.becker.freelance.commons.order;

import com.becker.freelance.math.Decimal;

public interface ConditionalOrder extends LazyOrder {

    public Order getDelegateOrder();

    public TriggerDirection getTriggerDirection();

    public Decimal getThresholdPrice();

    @Override
    default Decimal getNearestExecutionPrice() {
        return getThresholdPrice();
    }

}
