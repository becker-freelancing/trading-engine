package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingLowIndicator;
import com.becker.freelance.indicators.ta.swing.SwingLowPoint;
import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Comparator;

public class SupportIndicator extends ZoneIndicator<SwingLowPoint, Support> {


    public SupportIndicator(Num rangePrecision, int period, Indicator<Num> estimationIndicator) {
        super(rangePrecision, new SwingLowIndicator(period, estimationIndicator));
    }

    @Override
    protected Support map(Cluster<SwingLowPoint> cluster) {
        return new Support(
                cluster.items().stream().map(SwingPoint::index).min(Comparator.naturalOrder()).orElse(0),
                cluster.items().stream().map(SwingPoint::index).max(Comparator.naturalOrder()).orElse(Integer.MAX_VALUE),
                cluster.items().stream().map(SwingPoint::candleValue).min(Comparator.comparing(Num::doubleValue)).orElse(DecimalNum.ZERO),
                cluster.items().stream().map(SwingPoint::candleValue).max(Comparator.comparing(Num::doubleValue)).orElse(DecimalNum.valueOf(Long.MAX_VALUE)),
                DecimalNum.ZERO,
                cluster.count(),
                cluster.items()
        );
    }

}
