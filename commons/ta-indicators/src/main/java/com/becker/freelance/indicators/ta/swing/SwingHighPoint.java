package com.becker.freelance.indicators.ta.swing;

import org.ta4j.core.num.Num;

public record SwingHighPoint(boolean unstable, int index, Num candleValue) implements SwingPoint {
}
