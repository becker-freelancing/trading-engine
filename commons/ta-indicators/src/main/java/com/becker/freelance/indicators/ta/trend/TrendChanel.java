package com.becker.freelance.indicators.ta.trend;

import org.ta4j.core.num.Num;

import java.util.function.Function;

public record TrendChanel(Trend trend, Function<Integer, Num> highLine, Function<Integer, Num> lowLine,
                          Function<Integer, Num> midLine) {
}
