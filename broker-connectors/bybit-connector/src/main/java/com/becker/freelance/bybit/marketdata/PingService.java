package com.becker.freelance.bybit.marketdata;

import okhttp3.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class PingService {


    private static final String PING_DATA = "{\"op\":\"ping\"}";

    private static final Logger logger = LoggerFactory.getLogger(PingService.class);

    private static PingService instance;

    private WebSocket ws;
    private ScheduledExecutorService executorService;
    private boolean pinging;

    public PingService(WebSocket ws) {
        this.ws = ws;
        this.executorService = Executors.newScheduledThreadPool(1);
        pinging = false;
    }

    public static PingService getInstance(WebSocket ws) {
        if (instance == null) {
            instance = new PingService(ws);
        }

        instance.setWebsocket(ws);
        return instance;
    }

    public void startAutoPinging() {
        if (pinging) {
            return;
        }

        if (executorService.isShutdown()) {
            executorService = Executors.newScheduledThreadPool(1);
        }
        executorService.scheduleAtFixedRate(this::ping, 20, 30, TimeUnit.SECONDS);
        pinging = true;
    }

    public void stopAutoPinging() {
        if (!pinging) {
            return;
        }

        executorService.shutdownNow();
        pinging = false;
    }

    private void ping() {
        logger.debug("Executing ping...");
        ws.send(PING_DATA);
    }

    private void setWebsocket(WebSocket ws) {
        this.ws = ws;
    }
}
