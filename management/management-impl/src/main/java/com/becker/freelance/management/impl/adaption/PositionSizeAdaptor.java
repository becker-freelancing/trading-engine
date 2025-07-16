package com.becker.freelance.management.impl.adaption;

import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.management.api.adaption.EntrySignalAdaptor;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.commons.calculation.Calculator;
import com.becker.freelance.management.commons.calculation.PositionSizeCalculationParams;
import com.becker.freelance.management.commons.calculation.PositionSizeCalculator;
import com.becker.freelance.management.commons.calculation.PositionSizeSanitizer;
import com.becker.freelance.math.Decimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionSizeAdaptor implements EntrySignalAdaptor {

    private static final Logger logger = LoggerFactory.getLogger(PositionSizeAdaptor.class);

    private final Calculator<Decimal, PositionSizeCalculationParams> positionSizeCalculator;
    private final Calculator<Decimal, Decimal> positionSizeSanitizer;

    public PositionSizeAdaptor() {
        this.positionSizeCalculator = new PositionSizeCalculator();
        this.positionSizeSanitizer = new PositionSizeSanitizer();
    }

    @Override
    public EntrySignalBuilder adapt(ManagementEnvironmentProvider environmentProvider, EntrySignalBuilder entrySignal) {
        TimeSeriesEntry currentPrice = environmentProvider.getCurrentPrice(entrySignal.getPair());
        Decimal stopDistanceInPoints = entrySignal.getOpenOrderBuilder().getEstimatedExecutionLevel(currentPrice)
                .subtract(entrySignal.getStopOrderBuilder().getEstimatedExecutionLevel(currentPrice))
                .abs();

        logger.debug("Stop distance: {} points", stopDistanceInPoints);

        if (stopDistanceInPoints.isEqualToZero()){
            entrySignal.setSize(Decimal.ONE);
            return entrySignal;
        }
        PositionSizeCalculationParams positionSizeCalculationParams = new PositionSizeCalculationParams(stopDistanceInPoints, entrySignal.getPair());

        Decimal positionSize = positionSizeCalculator.calculate(environmentProvider, positionSizeCalculationParams);
        if (environmentProvider.getMaxPositionSize().isLessThan(positionSize)) {
            positionSize = environmentProvider.getMaxPositionSize();
        }
        positionSize = positionSizeSanitizer.calculate(environmentProvider, positionSize);

        logger.debug("Calculated Position Size: {}", positionSize);
        entrySignal.setSize(positionSize);

        return entrySignal;
    }
}
