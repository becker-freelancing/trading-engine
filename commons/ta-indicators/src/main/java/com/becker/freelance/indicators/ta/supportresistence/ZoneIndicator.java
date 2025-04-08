package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingIndicator;
import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

public abstract class ZoneIndicator<S extends SwingPoint, T extends Zone<S>> implements Indicator<List<T>> {

    private final Num rangePrecision;
    private final SwingIndicator<S> swingIndicator;
    private final double weightFactor;
    private final double quantile;


    protected ZoneIndicator(Num rangePrecision, SwingIndicator<S> swingIndicator) {
        this.rangePrecision = rangePrecision;
        this.swingIndicator = swingIndicator;
        weightFactor = -0.005;
        quantile = 0.8;
    }


    @Override
    public List<T> getValue(int index) {
        List<S> sortedSwingPoints = swingIndicator.recalculateAllStable(index)
                .filter(swingLowPoint -> swingLowPoint.index() <= index)
                .sorted(Comparator.comparing(SwingPoint::candleValue))
                .toList();


        List<Cluster<S>> cluster = getCluster(sortedSwingPoints, index);
        Integer lowerCountThreshold = getThreshold(cluster);
        return cluster.stream()
                .filter(c -> c.count() >= lowerCountThreshold)
                .map(this::map)
                .toList();
    }

    protected abstract T map(Cluster<S> sCluster);

    private Integer getThreshold(List<Cluster<S>> cluster) {
        List<Integer> sorted = cluster.stream()
                .flatMap(c -> {
                    List<Integer> nums = new ArrayList<>();
                    for (int i = 0; i < c.count(); i++) {
                        nums.add(c.count());
                    }
                    return nums.stream();
                })
                .sorted(Comparator.naturalOrder())
                .toList();
        int index = (int) (sorted.size() * quantile);

        return sorted.get(index);
    }

    private List<Cluster<S>> getCluster(List<S> sortedSwingPoints, int index) {
        Num minPrice = getMinPrice(index);
        Num maxPrice = getMaxPrice(index);
        Num zoneWidth = minPrice.plus(maxPrice).dividedBy(DecimalNum.valueOf(2)).multipliedBy(rangePrecision);

        List<Cluster<S>> result = new ArrayList<>();
        while (minPrice.isLessThan(maxPrice)) {

            Num finalMinPrice = minPrice;
            Num currentMaxPrice = minPrice.plus(zoneWidth);
            List<S> clusterContent = sortedSwingPoints.stream()
                    .filter(point -> point.candleValue().isGreaterThanOrEqual(finalMinPrice))
                    .filter(point -> point.candleValue().isLessThan(currentMaxPrice))
                    .toList();

            result.add(new Cluster<>(finalMinPrice, currentMaxPrice, clusterContent));

            minPrice = currentMaxPrice;
        }

        return result;
    }

    private Num getMaxPrice(int index) {
        BarSeries barSeries = swingIndicator.getBarSeries();
        return IntStream.range(0, index)
                .mapToObj(barSeries::getBar)
                .map(Bar::getClosePrice)
                .max(Comparator.comparing(Num::doubleValue))
                .orElse(DecimalNum.ZERO);
    }

    private Num getMinPrice(int index) {
        BarSeries barSeries = swingIndicator.getBarSeries();
        return IntStream.range(0, index)
                .mapToObj(barSeries::getBar)
                .map(Bar::getClosePrice)
                .min(Comparator.comparing(Num::doubleValue))
                .orElse(DecimalNum.ZERO);
    }

    protected record Cluster<S>(Num lowerLevel, Num upperLevel, List<S> items) {

        public int count() {
            return items.size();
        }
    }


    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public BarSeries getBarSeries() {
        return null;
    }

}
