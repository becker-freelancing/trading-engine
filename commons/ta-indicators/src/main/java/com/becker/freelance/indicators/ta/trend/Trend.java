package com.becker.freelance.indicators.ta.trend;

import com.becker.freelance.indicators.ta.swing.SwingHighPoint;
import com.becker.freelance.indicators.ta.swing.SwingLowPoint;

import java.util.List;

public class Trend {


    private final TrendDirection trendDirection;
    private final List<SwingHighPoint> lastNSwingHighs;
    private final List<SwingLowPoint> lastNSwingLows;

    public Trend(TrendDirection trendDirection, List<SwingHighPoint> lastNSwingHighs, List<SwingLowPoint> lastNSwingLows) {
        this.trendDirection = trendDirection;
        this.lastNSwingHighs = lastNSwingHighs;
        this.lastNSwingLows = lastNSwingLows;
    }

    public Trend(TrendDirection trendDirection) {
        this(trendDirection, List.of(), List.of());
    }

    public TrendDirection direction() {
        return trendDirection;
    }

    public List<SwingHighPoint> lastNSwingHighs() {
        return lastNSwingHighs;
    }

    public List<SwingLowPoint> lastNSwingLows() {
        return lastNSwingLows;
    }

    @Override
    public String toString() {
        return "Trend{" +
                "trendDirection=" + trendDirection +
                ", lastNSwingHighs=" + lastNSwingHighs +
                ", lastNSwingLows=" + lastNSwingLows +
                '}';
    }
}
