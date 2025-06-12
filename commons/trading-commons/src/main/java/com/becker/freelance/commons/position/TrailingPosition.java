package com.becker.freelance.commons.position;


import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface TrailingPosition extends Position {

    public default void setStopLevel(Decimal level, LocalDateTime currentTime) {
        getStopOrder().setExecutionLevel(level, currentTime);
    }

    public Decimal initialStopLevel();
}
