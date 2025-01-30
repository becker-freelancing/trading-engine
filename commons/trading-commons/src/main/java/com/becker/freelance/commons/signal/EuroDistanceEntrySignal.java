package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.math.Decimal;

public class EuroDistanceEntrySignal extends EntrySignal{

    private Decimal stopInEuros;
    private Decimal limitInEuros;


    public EuroDistanceEntrySignal(Decimal size, Direction direction, Decimal stopInEuros, Decimal limitInEuros,
                       PositionType positionType) {
        this(size, direction, stopInEuros, limitInEuros, positionType, null);
    }

    public EuroDistanceEntrySignal(Decimal size, Direction direction, Decimal stopInEuros, Decimal limitInEuros,
                       PositionType positionType, Decimal trailingStepSize) {

        super(size, direction, positionType, trailingStepSize);
        this.stopInEuros = stopInEuros;
        this.limitInEuros = limitInEuros;
    }

    public Decimal getStopInEuros() {
        return stopInEuros;
    }

    public Decimal getLimitInEuros() {
        return limitInEuros;
    }
}
