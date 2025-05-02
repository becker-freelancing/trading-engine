package com.becker.freelance.opentrades;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;

import java.util.List;

public interface OpenPositionRequestor {

    boolean isPositionOpen(Pair pair);

    public List<Position> getOpenPositions();
}
