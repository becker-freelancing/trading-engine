package com.becker.freelance.opentrades;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public interface ClosedTradesRequestor {

    public List<Trade> getTradesForDurationUntilTimeForPair(LocalDateTime toTime, Duration duration, Pair pair);
}
