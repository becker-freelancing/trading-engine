package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public record DefaultLevelEntrySignal(Decimal size,
                                      Direction direction,
                                      Pair pair,
                                      TimeSeriesEntry openPrice,
                                      PositionBehaviour positionBehaviour,
                                      TradeableQuantilMarketRegime openMarketRegime,
                                      Decimal stopLevel,
                                      Decimal limitLevel) implements EntrySignal {
}
