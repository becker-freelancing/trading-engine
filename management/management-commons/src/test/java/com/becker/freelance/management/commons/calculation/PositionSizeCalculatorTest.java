package com.becker.freelance.management.commons.calculation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class PositionSizeCalculatorTest {

    Calculator<Decimal, PositionSizeCalculationParams> positionSizeCalculator;

    @BeforeEach
    void setUp() {
        positionSizeCalculator = new PositionSizeCalculator();
    }

    @Test
    void withMaxRiskPerTradeAndSizeGreaterOne() {
        ManagementEnvironmentProvider environmentProvider = buildEnvironment(Decimal.valueOf(500),
                null,
                Decimal.valueOf(0.01));

        PositionSizeCalculationParams params = buildParams(
                Decimal.valueOf(10.),
                Decimal.valueOf(3));

        Decimal positionSize = positionSizeCalculator.calculate(environmentProvider, params);

        assertEquals(new Decimal("0.166667"), positionSize);
    }

    @Test
    void withPreferredRiskPerTradeAndSizeGreaterOne() {
        ManagementEnvironmentProvider environmentProvider = buildEnvironment(
                Decimal.valueOf(10000),
                Decimal.valueOf(0.005),
                Decimal.valueOf(0.01));

        PositionSizeCalculationParams params = buildParams(
                Decimal.valueOf(2.),
                Decimal.valueOf(7));

        Decimal positionSize = positionSizeCalculator.calculate(environmentProvider, params);

        assertEquals(new Decimal("3.571429"), positionSize);
    }


    PositionSizeCalculationParams buildParams(
            Decimal profitPerPointForOneContract,
            Decimal stopInPoints
    ) {

        Pair pair = mock(Pair.class);
        doReturn(profitPerPointForOneContract).when(pair).profitPerPointForOneContract();
        return new PositionSizeCalculationParams(
                stopInPoints,
                pair
        );
    }

    ManagementEnvironmentProvider buildEnvironment(
            Decimal accountBalance,
            Decimal preferredRiskPerTrade,
            Decimal maxRiskPerTrade
    ) {
        ManagementEnvironmentProvider environmentProvider = mock(ManagementEnvironmentProvider.class);
        doReturn(Optional.ofNullable(preferredRiskPerTrade)).when(environmentProvider).getPreferredRiskPerTrade();
        doReturn(accountBalance).when(environmentProvider).getCurrentAccountBalance();
        doReturn(maxRiskPerTrade).when(environmentProvider).getMaxRiskPerTrade();
        return environmentProvider;
    }

}