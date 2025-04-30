package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.management.api.EnvironmentProvider;
import com.becker.freelance.math.Decimal;

import java.math.RoundingMode;

public class PositionSizeSanitizer implements Calculator<Decimal, Decimal> {

    @Override
    public Decimal calculate(EnvironmentProvider environmentProvider, Decimal positionSize) {
        Integer maxBrokerOrderFractionPlaces = environmentProvider.getMaxBrokerOrderFractionPlaces();
        return positionSize.setScale(maxBrokerOrderFractionPlaces, RoundingMode.DOWN);
    }
}
