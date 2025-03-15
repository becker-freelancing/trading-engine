package com.becker.freelance.indicators.ta.supportresistence;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.num.Num;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SwingLowIndicator implements Indicator<Optional<SwingPoint>> {

    private final List<SwingPoint> indexes;
    private final Indicator<Num> sma;

    public SwingLowIndicator(Indicator<Num> source, int period) {
        indexes = new LinkedList<>();
        sma = new SMAIndicator(source, period);
    }


    @Override
    /**
     * Gibt den letzten SwingLow-Point zurück
     */
    public Optional<SwingPoint> getValue(int index) {
        Num current = sma.getValue(index);
        Num last = sma.getValue(index - 1);
        Num preLast = sma.getValue(index - 2);

        boolean low = current.isGreaterThan(last) && preLast.isGreaterThan(last);

        if (low) {
            indexes.add(new SwingPoint(index - 1));
        }

        if (indexes.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(indexes.get(indexes.size() - 1));
    }

    @Override
    public int getUnstableBars() {
        return sma.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return sma.getBarSeries();
    }

}
