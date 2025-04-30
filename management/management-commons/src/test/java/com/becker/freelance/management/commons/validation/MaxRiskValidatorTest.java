package com.becker.freelance.management.commons.validation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.management.api.EnvironmentProvider;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class MaxRiskValidatorTest {

    Validator<MaxRiskValidatorParams> maxRiskValidator;

    @BeforeEach
    void setUp() {
        maxRiskValidator = new MaxRiskValidator();
    }

    @Test
    void validIfNoMaxTotalRisk() {
        EnvironmentProvider environmentProvider = mock(EnvironmentProvider.class);
        doReturn(Optional.empty()).when(environmentProvider).getMaxTotalRisk();

        boolean valid = maxRiskValidator.isValid(environmentProvider, null);

        assertTrue(valid);
    }

    @Test
    void validIfTotalRiskIsLessThanMaxRisk() {
        EnvironmentProvider environmentProvider = buildEnvironment(Decimal.valueOf(2000), Decimal.valueOf(0.1705),
                buildPosition(buildPair(Decimal.valueOf(10)), Decimal.valueOf(10), Decimal.valueOf(8), Decimal.valueOf(0.1)),
                buildPosition(buildPair(Decimal.valueOf(3)), Decimal.valueOf(2), Decimal.valueOf(18), Decimal.valueOf(5)),
                buildPosition(buildPair(Decimal.valueOf(1000)), Decimal.valueOf(100), Decimal.valueOf(115), Decimal.valueOf(0.001)));

        MaxRiskValidatorParams maxRiskValidatorParams = new MaxRiskValidatorParams(Decimal.valueOf(30), Decimal.valueOf(0.01), buildPair(Decimal.valueOf(100)));

        boolean valid = maxRiskValidator.isValid(environmentProvider, maxRiskValidatorParams);

        assertTrue(valid);
    }

    @Test
    void notValidIfTotalRiskIsGreaterThanMaxRisk() {
        EnvironmentProvider environmentProvider = buildEnvironment(Decimal.valueOf(2000), Decimal.valueOf(0.1705),
                buildPosition(buildPair(Decimal.valueOf(10)), Decimal.valueOf(10), Decimal.valueOf(8), Decimal.valueOf(0.1)),
                buildPosition(buildPair(Decimal.valueOf(3)), Decimal.valueOf(2), Decimal.valueOf(18), Decimal.valueOf(5)),
                buildPosition(buildPair(Decimal.valueOf(1000)), Decimal.valueOf(100), Decimal.valueOf(115), Decimal.valueOf(0.001)));

        MaxRiskValidatorParams maxRiskValidatorParams = new MaxRiskValidatorParams(Decimal.valueOf(30), Decimal.valueOf(0.01), buildPair(Decimal.valueOf(101)));

        boolean valid = maxRiskValidator.isValid(environmentProvider, maxRiskValidatorParams);

        assertTrue(valid);
    }

    EnvironmentProvider buildEnvironment(
            Decimal currentAccountBalance,
            Decimal maxTotalRisk,
            Position... positions
    ) {
        EnvironmentProvider environmentProvider = mock(EnvironmentProvider.class);
        doReturn(currentAccountBalance).when(environmentProvider).getCurrentAccountBalance();
        doReturn(Optional.of(maxTotalRisk)).when(environmentProvider).getMaxTotalRisk();
        doReturn(List.of(positions)).when(environmentProvider).getOpenPositions();
        return environmentProvider;
    }

    Position buildPosition(
            Pair pair,
            Decimal stopLevel,
            Decimal openPrice,
            Decimal size
    ) {
        Position position = mock(Position.class);
        doReturn(pair).when(position).getPair();
        doReturn(stopLevel).when(position).getStopLevel();
        doReturn(openPrice).when(position).getOpenPrice();
        doReturn(size).when(position).getSize();
        return position;
    }

    Pair buildPair(Decimal profitPerPoint) {
        Pair pair = mock(Pair.class);
        doReturn(profitPerPoint).when(pair).profitPerPointForOneContract();
        return pair;
    }

}