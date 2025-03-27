package com.becker.freelance.capital.marketdata;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.ConversationContextHolder;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class PingService {

    private static Logger logger = LoggerFactory.getLogger(PingService.class);

    private static PingService instance;
    private final Session session;
    private boolean pinging;
    public PingService(Session session) {
        this.session = session;
        pinging = false;
    }

    public static PingService getInstance(Session session) {
        if (instance == null) {
            instance = new PingService(session);
        }

        return instance;
    }

    public void startAutoPinging() {
        if (pinging) {
            return;
        }

        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        scheduledExecutorService.scheduleAtFixedRate(this::ping, 1, 5, TimeUnit.MINUTES);
    }

    private void ping() {
        logger.info("Executing ping...");
        ConversationContext conversationContext = ConversationContextHolder.getConversationContext();

        String payload = String.format("""
                {
                    "destination": "ping",
                    "correlationId": "5",
                    "cst": "%s",
                    "securityToken": "%s"
                }
                """, conversationContext.clientSecurityToken(), conversationContext.accountSecurityToken());

        try {
            session.getBasicRemote().sendText(payload);
        } catch (IOException e) {
            logger.error("Error at executing ping", e);
        }
    }
}
