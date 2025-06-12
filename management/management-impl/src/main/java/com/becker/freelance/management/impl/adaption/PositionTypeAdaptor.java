package com.becker.freelance.management.impl.adaption;

import com.becker.freelance.commons.order.LimitOrderBuilder;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptor;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.math.Decimal;

public class PositionTypeAdaptor implements EntrySignalAdaptor {

    private static final Decimal DELTA = Decimal.valueOf(0.05);

    @Override
    public EntrySignalBuilder adapt(ManagementEnvironmentProvider environmentProvider, EntrySignalBuilder entrySignal) {
        TimeSeriesEntry currentPrice = environmentProvider.getCurrentPrice(entrySignal.getPair());

        Direction direction = entrySignal.getOpenOrderBuilder().getDirection();

        Decimal orderPrice = calculateOrderPrice(currentPrice, direction);
        LimitOrderBuilder openOrder = entrySignal.getOpenOrderBuilder().asLimitOrder()
                .withOrderPrice(orderPrice);

        entrySignal.withOpenOrder(openOrder);

        return entrySignal;
    }

    private Decimal calculateOrderPrice(TimeSeriesEntry currentPrice, Direction direction) {
        Decimal effectiveExecutionPrice = currentPrice.getClosePriceForDirection(direction);
        return switch (direction) {
            case BUY -> effectiveExecutionPrice.subtract(DELTA);
            case SELL -> effectiveExecutionPrice.add(DELTA);
        };
    }
}
