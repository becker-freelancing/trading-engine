package com.becker.freelance.opentrades;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;

import java.time.Duration;
import java.util.List;

public interface ClosedTradesRequestor {

    public List<Trade> getTradesForDurationUntilNowForPair(Duration duration, Pair pair);
}
