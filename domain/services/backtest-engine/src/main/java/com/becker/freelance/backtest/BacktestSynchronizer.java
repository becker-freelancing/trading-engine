package com.becker.freelance.backtest;

import com.becker.freelance.commons.timeseries.TimeUtil;
import com.becker.freelance.trading.external.services.backtest.candles.Synchronizer;
import com.becker.freelance.trading.external.services.candles.Synchronizeable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public class BacktestSynchronizer implements Synchronizer {

    private final Set<Synchronizeable> prioritySubscribers;
    private final Set<Synchronizeable> subscribers;
    private final LocalDateTime minTime;
    private final Duration timeShift;
    private final Predicate<LocalDateTime> validTimes;
    private LocalDateTime currentTime;


    public BacktestSynchronizer(LocalDateTime minTime, Duration timeShift, Predicate<LocalDateTime> validTimes) {
        this.minTime = minTime;
        this.timeShift = timeShift;
        this.validTimes = validTimes;
        this.subscribers = new LinkedHashSet<>();
        this.prioritySubscribers = new LinkedHashSet<>();
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
        currentTime = time;
        if (!validTimes.test(time)) {
            return;
        }
        prioritySubscribers.forEach(synchronizeable -> synchronizeable.synchronize(time));
        subscribers.forEach(synchronizeable -> synchronizeable.synchronize(time));
    }

    @Override
    public void addPrioritySubscriber(Synchronizeable synchronizeable) {
        if (prioritySubscribers.contains(synchronizeable)) {
            return;
        }
        subscribers.remove(synchronizeable);
        prioritySubscribers.add(synchronizeable);
    }

    @Override
    public LocalDateTime minTime() {
        return minTime;
    }

    @Override
    public void addSubscriber(Synchronizeable synchronizeable) {
        if (prioritySubscribers.contains(synchronizeable) || subscribers.contains(synchronizeable)) {
            return;
        }
        subscribers.add(synchronizeable);
    }

}
