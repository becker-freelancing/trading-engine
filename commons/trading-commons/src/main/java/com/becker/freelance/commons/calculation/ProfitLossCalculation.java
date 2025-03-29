package com.becker.freelance.commons.calculation;

import com.becker.freelance.math.Decimal;

public record ProfitLossCalculation(Decimal conversionRate, Decimal profit, Decimal closePrice) {
}
