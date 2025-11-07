package com.becker.freelance.trading.external.services.management.environment;

import com.becker.freelance.math.Decimal;

import java.time.Duration;

public record MaxDrawdown(Decimal maxDrawDownInPercent, Duration drawdownCalculationDuration) {
}
