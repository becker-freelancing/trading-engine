package com.becker.freelance.commons.position;


import com.becker.freelance.math.Decimal;

public interface TrailingPosition extends Position {

    public default void setStopLevel(Decimal level) {
        getStopOrder().setExecutionLevel(level);
    }

    public Decimal initialStopLevel();
}
