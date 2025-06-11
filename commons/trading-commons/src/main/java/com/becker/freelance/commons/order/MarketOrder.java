package com.becker.freelance.commons.order;

import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public interface MarketOrder extends Order {

    @Override
    default Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice) {
        return currentPrice.getClosePriceForDirection(getDirection());
    }
}
