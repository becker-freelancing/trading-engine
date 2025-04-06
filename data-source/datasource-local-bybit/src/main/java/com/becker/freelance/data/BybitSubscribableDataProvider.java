package com.becker.freelance.data;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class BybitSubscribableDataProvider extends SubscribableDataProvider {

    private static final AsyncValueHolder<Pair, TimeSeries> TIME_SERIES_HOLDER = new AsyncValueHolder<>();

    private final Set<DataSubscriber> subscribers;
    private final Pair pair;
    private final Synchronizer synchronizer;
    private TimeSeries timeSeries;

    public BybitSubscribableDataProvider(Pair pair, Synchronizer synchronizer) {
        this.pair = pair;
        this.synchronizer = synchronizer;
        this.subscribers = new LinkedHashSet<>();
        this.synchronizer.addSubscibor(this);
    }

    @Override
    public void addSubscriber(DataSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    private TimeSeries readTimeSeries() {
        Optional<LocalDateTime> minTime = synchronizer.minTime();
        Optional<LocalDateTime> maxTime = synchronizer.maxTime();

        BybitDataProvider bybitDataProvider = new BybitDataProvider(pair);
        return bybitDataProvider.readTimeSeries(
                minTime.orElse(LocalDateTime.MIN),
                maxTime.orElse(LocalDateTime.MAX)
        );
    }

    @Override
    public void synchronize(LocalDateTime time) {
        if (timeSeries == null) {
            this.timeSeries = TIME_SERIES_HOLDER.getOrRead(pair, this::readTimeSeries);
        }

        if (timeSeries.hasTime(time)) {
            subscribers.forEach(subscriber -> subscriber.consume(timeSeries, time));
        }
    }
}
