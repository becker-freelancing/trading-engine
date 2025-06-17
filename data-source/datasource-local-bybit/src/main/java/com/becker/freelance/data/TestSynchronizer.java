package com.becker.freelance.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestSynchronizer implements Synchronizer {

    private final List<Synchronizeable> s = new ArrayList<>();
    private final List<Synchronizeable> ps = new ArrayList<>();
    private BybitSubscribableDataProvider bybitSubscribableDataProvider = null;

    @Override
    public void addSubscriber(Synchronizeable synchronizeable) {
        s.add(synchronizeable);
    }

    @Override
    public void addPrioritySubscriber(Synchronizeable synchronizeable) {
        ps.add(synchronizeable);
    }

    @Override
    public Optional<LocalDateTime> minTime() {
        return Optional.of(LocalDateTime.of(2025, 6, 16, 19, 11));
    }

    @Override
    public Optional<LocalDateTime> maxTime() {
        return Optional.of(LocalDateTime.of(2025, 6, 17, 4, 19));
    }

    public void start() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            LocalDateTime min = minTime().get();
            LocalDateTime max = maxTime().get();

            while (min.isBefore(max)) {
                bybitSubscribableDataProvider.synchronize(min);
                min = min.plusMinutes(1);
            }
            System.out.println();
        }).start();
    }

    public void set(BybitSubscribableDataProvider bybitSubscribableDataProvider) {
        this.bybitSubscribableDataProvider = bybitSubscribableDataProvider;
    }
}
