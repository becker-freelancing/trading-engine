package com.becker.freelance.commons.order;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface LazyOrderBuilder extends OrderBuilder {

    @Override
    LazyOrder build(LocalDateTime currentTime);

    @Override
    LazyOrderBuilder withSize(Decimal size);

    @Override
    LazyOrderBuilder withDirection(Direction direction);

    @Override
    LazyOrderBuilder withPair(Pair pair);

    @Override
    LazyOrderBuilder withReduceOnly(boolean reduceOnly);
}
