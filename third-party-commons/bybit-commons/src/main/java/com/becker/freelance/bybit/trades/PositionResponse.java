package com.becker.freelance.bybit.trades;


import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public record PositionResponse(
        Pair pair,
        Decimal size,
        Direction direction,
        Decimal openPrice,
        LocalDateTime openTime,
        Decimal margin,
        Decimal stopLevel,
        Decimal limitLevel,
        String id,
        boolean isMarket,
        boolean isStopMarket,
        boolean isLimitMarket,
        PositionBehaviour positionType
) {
}
