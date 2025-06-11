package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.Optional;

public interface Order {

    public boolean isMarketOrder();

    public default boolean isLimitOrder() {
        return !isMarketOrder();
    }

    public Decimal getSize();

    public void setSize(Decimal size);

    public Direction getDirection();

    public Pair getPair();

    public boolean isReduceOnly();

    public boolean canBeExecuted(TimeSeriesEntry currentPrice);

    Decimal getEstimatedExecutionLevel(TimeSeriesEntry currentPrice);

    public boolean isExecuted();

    public Optional<Decimal> executionPrice();

    public Optional<LocalDateTime> executionTime();

    public void executeIfPossible(TimeSeriesEntry currentPrice);

    public Order clone();

}
