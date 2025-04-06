package com.becker.freelance.indicators.ta.swing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SwingLowIndicatorTest {

    SwingLowIndicator swingLowIndicator;
    Indicator<Num> estimationIndicator;

    @BeforeEach
    void setUp() {
        estimationIndicator = mock(Indicator.class);
        swingLowIndicator = new SwingLowIndicator(2, estimationIndicator);
    }

    @Test
    void testUnstableForSwingLow() {
        setupEstimationIndicator(3, 2, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(2);

        assertTrue(value.isPresent());
        assertTrue(value.get().unstable());
        assertEquals(DecimalNum.valueOf(1), value.get().candleValue());
        assertEquals(2, value.get().index());
    }

    @Test
    void testUnstableForSwingLow2() {
        setupEstimationIndicator(3, 2, 4, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(3);

        assertTrue(value.isPresent());
        assertTrue(value.get().unstable());
        assertEquals(DecimalNum.valueOf(1), value.get().candleValue());
        assertEquals(3, value.get().index());
    }

    @Test
    void testUnstableForNonSwingLow() {
        setupEstimationIndicator(3, 0.9, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(2);

        assertTrue(value.isEmpty());
    }

    @Test
    void testStableForSwingLow() {
        setupEstimationIndicator(1, 0.5, 0.49, 2, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(2);

        assertTrue(value.isPresent());
        assertFalse(value.get().unstable());
        assertEquals(DecimalNum.valueOf(0.49), value.get().candleValue());
        assertEquals(2, value.get().index());
    }

    @Test
    void testStableForSwingLow2() {
        setupEstimationIndicator(0.1, 2, 3.9, 1.9, 3, 2, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(3);

        assertTrue(value.isPresent());
        assertFalse(value.get().unstable());
        assertEquals(DecimalNum.valueOf(1.9), value.get().candleValue());
        assertEquals(3, value.get().index());
    }

    @Test
    void testStableForNonSwingLow() {
        setupEstimationIndicator(1, 2, 3, 4, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(2);

        assertTrue(value.isEmpty());
    }

    @Test
    void testStableForNonSwingLow2() {
        setupEstimationIndicator(1, 3.01, 3, 2, 1);

        Optional<SwingLowPoint> value = swingLowIndicator.getValue(2);

        assertTrue(value.isEmpty());
    }

    void setupEstimationIndicator(double... values) {
        for (int i = 0; i < values.length; i++) {
            doReturn(DecimalNum.valueOf(values[i])).when(estimationIndicator).getValue(i);
        }
        BarSeries barSeries = mock(BarSeries.class);
        doReturn(values.length).when(barSeries).getEndIndex();
        doReturn(barSeries).when(estimationIndicator).getBarSeries();
    }

}