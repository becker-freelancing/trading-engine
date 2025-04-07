package com.becker.freelance.indicators.ta.supportresistence;

import com.becker.freelance.indicators.ta.swing.SwingIndicator;
import com.becker.freelance.indicators.ta.swing.SwingPoint;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class ZoneIndicator<S extends SwingPoint, T extends Zone> implements Indicator<List<T>> {

    private final Num rangePrecision;
    private final SwingIndicator<S> swingIndicator;
    private final double weightFactor;


    public ZoneIndicator(Num rangePrecision, SwingIndicator<S> swingIndicator) {
        this.rangePrecision = rangePrecision;
        this.swingIndicator = swingIndicator;
        weightFactor = -0.005;
    }


    @Override
    public List<T> getValue(int index) {
        List<S> sortedSwingPoints = swingIndicator.recalculateAllStable(index)
                .filter(swingLowPoint -> swingLowPoint.index() <= index)
                .sorted(Comparator.comparing(SwingPoint::candleValue))
                .toList();


        List<Tupel<Num, List<S>>> priceLevelCluster = findPriceLevelCluster(sortedSwingPoints, index);
        Stream<Tupel<Num, List<S>>> scorePriceLevelCluster = calculateScores(priceLevelCluster);
        return map(scorePriceLevelCluster);
    }

    private List<T> map(Stream<Tupel<Num, List<S>>> scorePriceLevelCluster) {
        return scorePriceLevelCluster
                .map(this::map)
                .toList();
    }

    protected abstract T map(Tupel<Num, List<S>> scoreLevelCluster);


    private Stream<Tupel<Num, List<S>>> calculateScores(List<Tupel<Num, List<S>>> priceLevelCluster) {
        return priceLevelCluster.stream()
                .map(this::calcScore);
    }

    private Tupel<Num, List<S>> calcScore(Tupel<Num, List<S>> numListTupel) {
        List<S> sorted = numListTupel.second().stream().sorted(Comparator.comparing(SwingPoint::candleValue)).toList();
        Num zoneHeight = sorted.get(sorted.size() - 1).candleValue().minus(sorted.get(0).candleValue());
        Num score = numListTupel.first().multipliedBy(DecimalNum.valueOf(1).dividedBy(zoneHeight));
        return new Tupel<>(score, numListTupel.second());
    }

    private double getWeight(int currentIndex, SwingPoint swingLowPoint) {
        int timeDelta = currentIndex - swingLowPoint.index();
        return Math.exp(weightFactor * timeDelta);
    }


    protected Num getMaxPriceForCurrentCluster(Num averagePriceInCluster) {
        return averagePriceInCluster.plus(averagePriceInCluster.multipliedBy(rangePrecision));
    }

    protected Num getMinPriceForCurrentCluster(Num averagePriceInCluster) {
        return averagePriceInCluster.minus(averagePriceInCluster.multipliedBy(rangePrecision));
    }

    protected Tupel<Num, Num> getAveragePriceInCluster(List<S> currentCluster, int index) {
        Num weightedPriceSum = DecimalNum.ZERO;
        Num weightSum = DecimalNum.ZERO;
        for (SwingPoint swingLowPoint : currentCluster) {
            DecimalNum weight = DecimalNum.valueOf(getWeight(index, swingLowPoint));
            weightSum = weightSum.plus(weight);
            weightedPriceSum = weightedPriceSum.plus(swingLowPoint.candleValue().multipliedBy(weight));
        }

        return new Tupel<>(weightedPriceSum.dividedBy(weightSum), weightSum);
    }

    protected List<Tupel<Num, List<S>>> findPriceLevelCluster(List<S> sortedSwingPoints, int index) {
        List<Tupel<Num, List<S>>> cluster = new ArrayList<>();

        for (int i = 0; i < sortedSwingPoints.size(); i++) {

            List<S> currentCluster = new ArrayList<>();
            Num totalWeight = DecimalNum.valueOf(0);
            S startPoint = sortedSwingPoints.get(i);
            currentCluster.add(startPoint);

            for (int j = i + 1; j < sortedSwingPoints.size(); j++) {

                Tupel<Num, Num> averagePriceInCluster = getAveragePriceInCluster(currentCluster, index);
                Num maxPriceForSameCluster = getMaxPriceForCurrentCluster(averagePriceInCluster.first());
                Num minPriceForSameCluster = getMinPriceForCurrentCluster(averagePriceInCluster.first());

                S currentSwingPoint = sortedSwingPoints.get(j);
                Num currentValue = currentSwingPoint.candleValue();
                if (currentValue.isGreaterThan(maxPriceForSameCluster) || currentValue.isLessThan(minPriceForSameCluster)) {
                    break;
                }
                currentCluster.add(currentSwingPoint);
                totalWeight = averagePriceInCluster.second();
                i = j;
            }

            cluster.add(new Tupel<>(totalWeight, currentCluster));
        }
        return cluster;
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public BarSeries getBarSeries() {
        return null;
    }

    protected record Tupel<K, V>(K first, V second) {
    }
}
