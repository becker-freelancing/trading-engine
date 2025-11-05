package com.becker.freelance.backtest;

import com.becker.freelance.data.Synchronizeable;
import com.becker.freelance.data.Synchronizer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public class BacktestSynchronizer implements Synchronizer {

    private final LocalDateTime minTime;
    private final LocalDateTime maxTime;
    private final Set<Synchronizeable> prioritySubscribers;
    private final Set<Synchronizeable> subscribers;
    private LocalDateTime currentTime;


    public BacktestSynchronizer(LocalDateTime minTime, LocalDateTime maxTime) {
        this.subscribers = new LinkedHashSet<>();
        this.prioritySubscribers = new LinkedHashSet<>();
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.currentTime = LocalDateTime.of(minTime.getYear(), minTime.getMonth(), minTime.getDayOfMonth(), minTime.getHour(), minTime.getMinute());
    }

    public LocalDateTime getCurrentTime() {
        return currentTime;
    }

    public void shiftOneMinute() {
        shiftTime(Duration.ofMinutes(1));
    }

    public void shiftTime(Duration duration) {
        setTime(currentTime.plus(duration));
    }

    public void setTime(LocalDateTime time) {
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
