package com.becker.freelance.indicators.ta.trend;

import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class TrendChanelIndicator implements Indicator<Optional<TrendChanel>> {

    private final TrendIndicator trendIndicator;

    public TrendChanelIndicator(TrendIndicator trendIndicator) {
        this.trendIndicator = trendIndicator;
    }

    @Override
    public Optional<TrendChanel> getValue(int index) {
        Trend trend = trendIndicator.getValue(index);

        if (trend.direction() == TrendDirection.UP) {
            return Optional.of(
                    calculateTrendChanel(trend)
            );
        } else if (trend.direction() == TrendDirection.DOWN) {
            return Optional.of(
                    calculateTrendChanel(trend)
            );
        }

        return Optional.empty();
    }

    private TrendChanel calculateTrendChanel(Trend trend) {
        TrendChanelOuterLine highLine = regression(trend.lastNSwingHighs());
        TrendChanelOuterLine lowLine = regression(trend.lastNSwingLows());
        TrendChanelMidLine midLine = new TrendChanelMidLine(highLine, lowLine);

        return new TrendChanel(
                trend,
                highLine,
                lowLine,
                midLine
        );
    }

    private TrendChanelOuterLine regression(List<? extends SwingPoint> points) {
        Num sumX = DecimalNum.ZERO;
        Num sumY = DecimalNum.ZERO;
        Num sumXY = DecimalNum.ZERO;
        Num sumX2 = DecimalNum.ZERO;

        for (SwingPoint point : points) {
            Num x = DecimalNum.valueOf(point.index());
            sumX = sumX.plus(x);
            sumY = sumY.plus(point.candleValue());
            sumXY = sumXY.plus(x.multipliedBy(point.candleValue()));
            sumX2 = sumX2.plus(x.multipliedBy(x));
        }

        DecimalNum size = DecimalNum.valueOf(points.size());
        Num meanX = sumX.dividedBy(size);
        Num meanY = sumY.dividedBy(size);

        Num mZ = sumXY.minus(size.multipliedBy(meanX).multipliedBy(meanY));
        Num mD = sumX2.minus(size.multipliedBy(meanX).multipliedBy(meanX));
        Num m = mZ.dividedBy(mD);
        Num c = meanY.minus(m.multipliedBy(meanX));

        return new TrendChanelOuterLine(m, c);
    }

    @Override
    public int getUnstableBars() {
        return trendIndicator.getUnstableBars();
    }

    @Override
    public BarSeries getBarSeries() {
        return trendIndicator.getBarSeries();
    }

    private record TrendChanelOuterLine(Num m, Num c) implements Function<Integer, Num> {
        @Override
        public Num apply(Integer index) {
            return m.multipliedBy(DecimalNum.valueOf(index)).plus(c);
        }
    }

    private record TrendChanelMidLine(TrendChanelOuterLine upper,
                                      TrendChanelOuterLine lower) implements Function<Integer, Num> {
        @Override
        public Num apply(Integer index) {
            Num sum = upper.apply(index).plus(lower.apply(index));
            return sum.dividedBy(DecimalNum.valueOf(2));
        }
    }
}
