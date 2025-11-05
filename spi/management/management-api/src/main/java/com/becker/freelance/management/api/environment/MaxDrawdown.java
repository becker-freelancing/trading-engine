package com.becker.freelance.management.api.environment;

import com.becker.freelance.math.Decimal;

import java.time.Duration;

public record MaxDrawdown(Decimal maxDrawDownInPercent, Duration drawdownCalculationDuration) {
}
