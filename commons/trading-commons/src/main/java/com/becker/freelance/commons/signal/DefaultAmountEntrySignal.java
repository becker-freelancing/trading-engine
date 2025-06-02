package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public record DefaultAmountEntrySignal(Decimal size,
                                       Direction direction,
                                       Pair pair,
                                       TimeSeriesEntry openPrice,
                                       PositionType positionType,
                                       TradeableQuantilMarketRegime openMarketRegime,
                                       Decimal stopAmount,
                                       Decimal limitAmount) implements AmountEntrySignal {
}
