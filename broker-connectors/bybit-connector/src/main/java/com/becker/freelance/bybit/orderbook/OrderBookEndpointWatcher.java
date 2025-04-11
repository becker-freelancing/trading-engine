package com.becker.freelance.bybit.orderbook;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class OrderBookEndpointWatcher {

    private static final Duration MAX_DURATION_FOR_RECONNECT = Duration.ofMinutes(1);

    private final Supplier<LocalDateTime> lastUpdateTimeSupplier;
    private final ScheduledExecutorService executorService;


    public OrderBookEndpointWatcher(Supplier<LocalDateTime> lastUpdateTimeSupplier) {
        this.lastUpdateTimeSupplier = lastUpdateTimeSupplier;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    public void watchUpdateTime() {

        executorService.scheduleAtFixedRate(this::watch, 20, 30, TimeUnit.SECONDS);
    }

    public void stopWatching() {
        executorService.shutdown();
    }

    private void watch() {
        LocalDateTime lastUpdateTime = lastUpdateTimeSupplier.get();
        LocalDateTime now = LocalDateTime.now();
        long between = ChronoUnit.SECONDS.between(lastUpdateTime, now);
        long maxDuration = MAX_DURATION_FOR_RECONNECT.get(ChronoUnit.SECONDS);

        if (between > maxDuration) {
            OrderbookSocketRegistry.reconnectAll();
        }
    }


}
