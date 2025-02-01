package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.math.Decimal;

public class LevelEntrySignal extends EntrySignal{


    private final Decimal stopLevel;
    private final Decimal limitLevel;


    public LevelEntrySignal(Decimal size, Direction direction, Decimal stopLevel, Decimal limitLevel,
                                   PositionType positionType) {
        this(size, direction, stopLevel, limitLevel, positionType, null);
    }

    public LevelEntrySignal(Decimal size, Direction direction, Decimal stopLevel, Decimal limitLevel,
                                   PositionType positionType, Decimal trailingStepSize) {

        super(size, direction, positionType, trailingStepSize);
        this.stopLevel = stopLevel;
        this.limitLevel = limitLevel;
    }

    public Decimal getStopLevel() {
        return stopLevel;
    }

    public Decimal getLimitLevel() {
        return limitLevel;
    }
}
