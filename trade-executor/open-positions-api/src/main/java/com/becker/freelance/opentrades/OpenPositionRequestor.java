package com.becker.freelance.opentrades;

import com.becker.freelance.commons.pair.Pair;

public interface OpenPositionRequestor {

    boolean isPositionOpen(Pair pair);
}
