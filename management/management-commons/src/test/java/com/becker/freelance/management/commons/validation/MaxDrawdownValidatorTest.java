package com.becker.freelance.management.commons.validation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.environment.MaxDrawdown;
import com.becker.freelance.math.Decimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class MaxDrawdownValidatorTest {

    Validator<Pair> maxDrawdownValidator;

    @BeforeEach
    void setUp() {
        maxDrawdownValidator = new MaxDrawdownValidator();
    }

    @Test
    void validIfLessThanMaxDrawdown() {
        ManagementEnvironmentProvider environmentProvider = mock(ManagementEnvironmentProvider.class);
        doReturn(List.of(
                new MaxDrawdown(Decimal.valueOf(25), Duration.ZERO),
                new MaxDrawdown(Decimal.valueOf(23.48), Duration.ZERO)
        )).when(environmentProvider).getMaxDrawdowns();
        doReturn(buildTrades()).when(environmentProvider).getTradesForDurationUntilNowForPair(any(), any());
        doReturn(Decimal.valueOf(940)).when(environmentProvider).getCurrentAccountBalance();

        boolean valid = maxDrawdownValidator.isValid(environmentProvider, mock(Pair.class));

        assertTrue(valid);
    }

    @Test
    void notValidIfLessThanMaxDrawdown() {
        ManagementEnvironmentProvider environmentProvider = mock(ManagementEnvironmentProvider.class);
        doReturn(List.of(
                new MaxDrawdown(Decimal.valueOf(25), Duration.ZERO),
                new MaxDrawdown(Decimal.valueOf(23.47), Duration.ZERO)
        )).when(environmentProvider).getMaxDrawdowns();
        doReturn(buildTrades()).when(environmentProvider).getTradesForDurationUntilNowForPair(any(), any());
        doReturn(Decimal.valueOf(940)).when(environmentProvider).getCurrentAccountBalance();

        boolean valid = maxDrawdownValidator.isValid(environmentProvider, mock(Pair.class));

        assertFalse(valid);
    }

    private List<Trade> buildTrades() {
        return List.of(
                buildTrade(100),
                buildTrade(50),
                buildTrade(-200),
                buildTrade(80),
                buildTrade(-150),
                buildTrade(60)
        );
    }

    Trade buildTrade(int profit) {
        Trade trade = mock(Trade.class);
        doReturn(Decimal.valueOf(profit)).when(trade).getProfitInEuroWithFees();
        return trade;
    }

}