package com.becker.freelance.indicators.ta.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RollingMeanIndicatorTest {

    Indicator<Optional<Num>> baseIndicator;
    Indicator<Optional<Num>> rollingMeanIndicator;

    @BeforeEach
    void setUp() {
        Map<Integer, Integer> baseValues = Map.of(
                0, 1,
                1, 2,
                2, 3,
                3, 4,
                5, 2,
                6, 1,
                7, 12,
                8, 5
        );
        baseIndicator = new Indicator<Optional<Num>>() {
            @Override
            public Optional<Num> getValue(int index) {
                return Optional.ofNullable(baseValues.get(index)).map(DecimalNum::valueOf);
            }

            @Override
            public int getUnstableBars() {
                return 0;
            }

            @Override
            public BarSeries getBarSeries() {
                return null;
            }
        };
        rollingMeanIndicator = new RollingMeanIndicator(baseIndicator, 3);
    }

    @Test
    void getValue() {
        assertEquals(Optional.empty(), rollingMeanIndicator.getValue(0));
        assertEquals(Optional.empty(), rollingMeanIndicator.getValue(1));
        assertEquals(Optional.of(DecimalNum.valueOf(2)), rollingMeanIndicator.getValue(2));
        assertEquals(Optional.of(DecimalNum.valueOf(3)), rollingMeanIndicator.getValue(3));
    }

    @Test
    void getValueWithGap() {
        assertEquals(Optional.empty(), rollingMeanIndicator.getValue(0));
        assertEquals(Optional.empty(), rollingMeanIndicator.getValue(1));
        assertEquals(Optional.of(DecimalNum.valueOf(2)), rollingMeanIndicator.getValue(2));
        assertEquals(Optional.of(DecimalNum.valueOf(3)), rollingMeanIndicator.getValue(3));
        assertEquals(Optional.empty(), rollingMeanIndicator.getValue(5));
        assertEquals(Optional.empty(), rollingMeanIndicator.getValue(6));
        assertEquals(Optional.of(DecimalNum.valueOf(5)), rollingMeanIndicator.getValue(7));
        assertEquals(Optional.of(DecimalNum.valueOf(6)), rollingMeanIndicator.getValue(8));
    }
}