package com.becker.freelance.management.api;

import com.becker.freelance.math.Decimal;

import java.time.Duration;

public record MaxDrawdown(Decimal maxDrawDownInPercent, Duration drawdownCalculationDuration) {
}
