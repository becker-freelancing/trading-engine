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

class SwingHighIndicatorTest {

    SwingHighIndicator swingHighIndicator;
    Indicator<Num> estimationIndicator;

    @BeforeEach
    void setUp() {
        estimationIndicator = mock(Indicator.class);
        swingHighIndicator = new SwingHighIndicator(2, estimationIndicator);
    }

    @Test
    void testUnstableForSwingHigh() {
        setupEstimationIndicator(1, 2, 3);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(2);

        assertTrue(value.isPresent());
        assertTrue(value.get().unstable());
        assertEquals(DecimalNum.valueOf(3), value.get().candleValue());
        assertEquals(2, value.get().index());
    }

    @Test
    void testUnstableForSwingHigh2() {
        setupEstimationIndicator(1, 2, 3, 4);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(3);

        assertTrue(value.isPresent());
        assertTrue(value.get().unstable());
        assertEquals(DecimalNum.valueOf(4), value.get().candleValue());
        assertEquals(3, value.get().index());
    }

    @Test
    void testUnstableForNonSwingHigh() {
        setupEstimationIndicator(1, 4, 3);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(2);

        assertTrue(value.isEmpty());
    }

    @Test
    void testStableForSwingHigh() {
        setupEstimationIndicator(1, 2, 3, 0.5, 1);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(2);

        assertTrue(value.isPresent());
        assertFalse(value.get().unstable());
        assertEquals(DecimalNum.valueOf(3), value.get().candleValue());
        assertEquals(2, value.get().index());
    }

    @Test
    void testStableForSwingHigh2() {
        setupEstimationIndicator(1, 2, 3, 4, 3.9, 2, 9);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(3);

        assertTrue(value.isPresent());
        assertFalse(value.get().unstable());
        assertEquals(DecimalNum.valueOf(4), value.get().candleValue());
        assertEquals(3, value.get().index());
    }

    @Test
    void testStableForNonSwingHigh() {
        setupEstimationIndicator(1, 4, 3, 2, 1);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(2);

        assertTrue(value.isEmpty());
    }

    @Test
    void testStableForNonSwingHigh2() {
        setupEstimationIndicator(1, 2, 3, 3.01, 1);

        Optional<SwingHighPoint> value = swingHighIndicator.getValue(2);

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