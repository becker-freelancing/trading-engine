package com.becker.freelance.management.commons.validation;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.management.api.environment.ManagementEnvironmentProvider;
import com.becker.freelance.management.api.environment.MaxDrawdown;
import com.becker.freelance.math.Decimal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class MaxDrawdownValidator implements Validator<Pair> {

    @Override
    public boolean isValid(ManagementEnvironmentProvider environmentProvider, Pair pair) {

        List<MaxDrawdown> maxDrawDowns = environmentProvider.getMaxDrawdowns();
        return maxDrawDowns.stream().allMatch(maxDrawdown -> isLessThanDrawDown(maxDrawdown, environmentProvider, pair));
    }

    private boolean isLessThanDrawDown(MaxDrawdown maxDrawdown, ManagementEnvironmentProvider environmentProvider, Pair pair) {
        List<Trade> tradesForDurationUntilNow = environmentProvider.getTradesForDurationUntilNowForPair(maxDrawdown.drawdownCalculationDuration(), pair);
        Decimal accountBalance = environmentProvider.getCurrentAccountBalance();
        Decimal[] accountBalancesAfterTrades = new Decimal[tradesForDurationUntilNow.size() + 1];
        accountBalancesAfterTrades[accountBalancesAfterTrades.length - 1] = accountBalance;
        for (int i = tradesForDurationUntilNow.size() - 1; i >= 0; i--) {
            accountBalance = accountBalance.subtract(tradesForDurationUntilNow.get(i).getProfitInEuro());
            accountBalancesAfterTrades[i] = accountBalance;
        }
        Decimal maxAccountBalance = Arrays.stream(accountBalancesAfterTrades).max(Comparator.naturalOrder()).orElse(accountBalance);
        Decimal actualMaxDrawdown = Arrays.stream(accountBalancesAfterTrades)
                .map(accountBalancesAfterTrade -> calcDrawDown(accountBalancesAfterTrade, maxAccountBalance))
                .max(Comparator.naturalOrder())
                .orElse(Decimal.ZERO);

        return actualMaxDrawdown.isLessThanOrEqualTo(maxDrawdown.maxDrawDownInPercent());
    }

    private Decimal calcDrawDown(Decimal accountBalancesAfterTrade, Decimal maxAccountBalance) {
        Decimal diff = maxAccountBalance.subtract(accountBalancesAfterTrade);
        return diff.divide(maxAccountBalance).multiply(Decimal.valueOf(100));
    }
}
