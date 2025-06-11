package com.becker.freelance.commons.order;

import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public interface LimitOrder extends LazyOrder {

    public Decimal getOrderPrice();

    @Override
    default Decimal getNearestExecutionPrice() {
        return getOrderPrice();
    }

    @Override
    default Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        return getOrderPrice();
    }
}
