package com.becker.freelance.management.impl.adaption;

import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptor;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.commons.calculation.Calculator;
import com.becker.freelance.management.commons.calculation.PositionSizeCalculationParams;
import com.becker.freelance.management.commons.calculation.PositionSizeCalculator;
import com.becker.freelance.management.commons.calculation.PositionSizeSanitizer;
import com.becker.freelance.math.Decimal;

public class PositionSizeAdaptor implements EntrySignalAdaptor {

    private final Calculator<Decimal, PositionSizeCalculationParams> positionSizeCalculator;
    private final Calculator<Decimal, Decimal> positionSizeSanitizer;

    public PositionSizeAdaptor() {
        this.positionSizeCalculator = new PositionSizeCalculator();
        this.positionSizeSanitizer = new PositionSizeSanitizer();
    }

    @Override
    public EntrySignal adapt(ManagementEnvironmentProvider environmentProvider, EntrySignal entrySignal) {
        LevelEntrySignal levelEntrySignal = entrySignal.toLevelEntrySignal(environmentProvider.getEurUsdRequestor());

        Decimal stopDistanceInPoints = levelEntrySignal.stopInPoints();
        PositionSizeCalculationParams positionSizeCalculationParams = new PositionSizeCalculationParams(stopDistanceInPoints, entrySignal.pair());

        Decimal positionSize = positionSizeCalculator.calculate(environmentProvider, positionSizeCalculationParams);
        positionSize = positionSizeSanitizer.calculate(environmentProvider, positionSize);

        return new LevelEntrySignalImpl(levelEntrySignal, positionSize);
    }
}
