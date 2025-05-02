package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.math.Decimal;

import java.math.RoundingMode;

public class PositionSizeSanitizer implements Calculator<Decimal, Decimal> {

    @Override
    public Decimal calculate(ManagementEnvironmentProvider environmentProvider, Decimal positionSize) {
        Integer maxBrokerOrderFractionPlaces = environmentProvider.getMaxBrokerOrderFractionPlaces();
        return positionSize.setScale(maxBrokerOrderFractionPlaces, RoundingMode.DOWN);
    }
}
