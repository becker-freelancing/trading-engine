package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.num.Num;

public record SwingLowPoint(boolean unstable, int index, Num candleValue) {
}
