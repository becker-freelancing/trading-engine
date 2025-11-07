package com.becker.freelance.backtest;

import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.trading.external.services.backtest.candles.Synchronizer;
import com.becker.freelance.trading.external.services.candles.Synchronizeable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public class BacktestSynchronizer implements Synchronizer {

    private final LocalDateTime minTime;
    private final LocalDateTime maxTime;
    private final Set<Synchronizeable> prioritySubscribers;
    private final Set<Synchronizeable> subscribers;
    private final Duration timeShift;
    private final Predicate<LocalDateTime> validTimes;
    private LocalDateTime currentTime;


    public BacktestSynchronizer(LocalDateTime minTime, LocalDateTime maxTime, Duration timeShift, Predicate<LocalDateTime> validTimes) {
        this.timeShift = timeShift;
        this.validTimes = validTimes;
        this.subscribers = new LinkedHashSet<>();
        this.prioritySubscribers = new LinkedHashSet<>();
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.currentTime = TimeUtil.nextAligned(minTime, timeShift);
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void shiftTime() {
        shiftTime(timeShift);
    }

    public void shiftTime(Duration duration) {
        setTime(currentTime.plus(duration));
    }

    public void setTime(LocalDateTime time) {
        if (!validTimes.test(time)) {
            return;
        }
        currentTime = time;
        prioritySubscribers.forEach(synchronizeable -> synchronizeable.synchronize(time));
        subscribers.forEach(synchronizeable -> synchronizeable.synchronize(time));
    }

    @Override
    public void addPrioritySubscriber(Synchronizeable synchronizeable) {
        prioritySubscribers.add(synchronizeable);
    }

    @Override
    public void addSubscriber(Synchronizeable synchronizeable) {
        subscribers.add(synchronizeable);
    }

    @Override
    public Optional<LocalDateTime> minTime() {
        return Optional.of(minTime);
    }

    @Override
    public Optional<LocalDateTime> maxTime() {
        return Optional.of(maxTime);
    }
}
