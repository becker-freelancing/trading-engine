package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingHighIndicator;
import com.becker.freelance.indicators.ta.swing.SwingHighPoint;
import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Comparator;

public class ResistenceIndicator extends ZoneIndicator<SwingHighPoint, Resistence> {


    public ResistenceIndicator(Num rangePrecision, int period, Indicator<Num> estimationIndicator) {
        super(rangePrecision, new SwingHighIndicator(period, estimationIndicator));
    }

    @Override
    protected Resistence map(Cluster<SwingHighPoint> cluster) {
        return new Resistence(
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
