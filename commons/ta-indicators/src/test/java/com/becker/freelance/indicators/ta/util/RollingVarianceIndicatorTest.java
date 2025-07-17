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
import static org.junit.jupiter.api.Assertions.assertTrue;

class RollingVarianceIndicatorTest {

    Indicator<Optional<Num>> baseIndicator;
    Indicator<Optional<Num>> rollingVarIndicator;

    @BeforeEach
    void setUp() {
        Map<Integer, Integer> baseValues = Map.of(
                0, 1,
                1, 2,
                2, 3,
                3, 13,
                5, 2,
                6, 1,
                7, 12,
                8, 5
        );
        baseIndicator = new Indicator<>() {
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
        rollingVarIndicator = new RollingVarianceIndicator(baseIndicator, 3);
    }

    @Test
    void getValueWithGap() {
        assertEquals(Optional.empty(), rollingVarIndicator.getValue(0));
        assertEquals(Optional.empty(), rollingVarIndicator.getValue(1));
        assertAlmostEquals(DecimalNum.valueOf(2 / 3.), rollingVarIndicator.getValue(2));
        assertAlmostEquals(DecimalNum.valueOf(74 / 3.), rollingVarIndicator.getValue(3));
        assertEquals(Optional.empty(), rollingVarIndicator.getValue(5));
        assertEquals(Optional.empty(), rollingVarIndicator.getValue(6));
        assertAlmostEquals(DecimalNum.valueOf(74 / 3.), rollingVarIndicator.getValue(7));
        assertAlmostEquals(DecimalNum.valueOf(62 / 3.), rollingVarIndicator.getValue(8));
    }

    void assertAlmostEquals(Num expected, Optional<Num> actual) {
        assertTrue(actual.isPresent());
        Num num = actual.get();
        Num delta = DecimalNum.valueOf(0.000000001);
        assertTrue(num.isLessThan(expected.plus(delta)));
        assertTrue(num.isGreaterThan(expected.minus(delta)));
    }
}