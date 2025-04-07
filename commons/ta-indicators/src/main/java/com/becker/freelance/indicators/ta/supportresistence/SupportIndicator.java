package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingLowIndicator;
import com.becker.freelance.indicators.ta.swing.SwingLowPoint;
import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.Comparator;
import java.util.List;

public class SupportIndicator extends ZoneIndicator<SwingLowPoint, Support> {


    public SupportIndicator(Num rangePrecision, int period, Indicator<Num> estimationIndicator) {
        super(rangePrecision, new SwingLowIndicator(period, estimationIndicator));
    }

    @Override
    protected Support map(Tupel<Num, List<SwingLowPoint>> scoreLevelCluster) {
        List<SwingLowPoint> cluster = scoreLevelCluster.second();
        return new Support(
                cluster.stream().map(SwingPoint::index).min(Comparator.naturalOrder()).orElse(0),
                cluster.stream().map(SwingPoint::index).max(Comparator.naturalOrder()).orElse(Integer.MAX_VALUE),
                cluster.stream().map(SwingPoint::candleValue).min(Comparator.comparing(Num::doubleValue)).orElse(DecimalNum.ZERO),
                cluster.stream().map(SwingPoint::candleValue).max(Comparator.comparing(Num::doubleValue)).orElse(DecimalNum.valueOf(Long.MAX_VALUE)),
                scoreLevelCluster.first(),
                cluster.size(),
                cluster
        );
    }

}
