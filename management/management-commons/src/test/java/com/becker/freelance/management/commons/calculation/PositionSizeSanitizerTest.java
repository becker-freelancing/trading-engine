package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.management.api.EnvironmentProvider;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class PositionSizeSanitizerTest {

    Calculator<Decimal, Decimal> positionSizeSanitizer;

    @BeforeEach
    void setUp() {
        positionSizeSanitizer = new PositionSizeSanitizer();
    }

    @Test
    void sanitizePositionSizeForFractionsInRange() {
        EnvironmentProvider environmentProvider = buildEnvironment(5);

        Decimal sanitize = positionSizeSanitizer.calculate(environmentProvider, new Decimal("3.40005"));

        assertEquals(new Decimal("3.40005"), sanitize);
    }


    @Test
    void sanitizePositionSizeForFractionsNotInRange() {
        EnvironmentProvider environmentProvider = buildEnvironment(5);

        Decimal sanitize = positionSizeSanitizer.calculate(environmentProvider, new Decimal("3.400005"));

        assertEquals(new Decimal("3.40000"), sanitize);
    }

    private EnvironmentProvider buildEnvironment(int brokerFractions) {
        EnvironmentProvider environmentProvider = mock(EnvironmentProvider.class);
        doReturn(brokerFractions).when(environmentProvider).getMaxBrokerOrderFractionPlaces();
        return environmentProvider;
    }

}