package com.becker.freelance.capital.marketdata;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.util.Constants;
import com.becker.freelance.capital.util.PairConverter;
import com.becker.freelance.commons.pair.Pair;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

class MarketDataEndpoint extends Endpoint {

    private final Logger logger = LoggerFactory.getLogger(MarketDataEndpoint.class.getName());
    private final MarketDataMessageHandler messageHandler;
    private final Runnable onClose;
    private final Runnable onOpen;
    private Session session;
    private boolean connected;

    public MarketDataEndpoint(Consumer<BidMarketData> bidConsumer, Consumer<AskMarketData> askConsumer, Runnable onClose, Runnable onOpen) {
        this.onClose = onClose;
        this.onOpen = onOpen;
        messageHandler = new MarketDataMessageHandler(bidConsumer, askConsumer);
        connected = false;
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        logger.info("Websocket-Connection successfully opened. Config: {}, Session: {}", endpointConfig, session);
        this.session = session;
        this.session.addMessageHandler(messageHandler);
        connected = true;
        onOpen.run();
    }

    public void subscribeOHLC(ConversationContext conversationContext, Set<Pair> pairs) throws IOException, URISyntaxException, DeploymentException {
        if (!connected) {
            connect(conversationContext);
        }
        subscribeTo(conversationContext, pairs);
        PingService.getInstance(session).startAutoPinging();
    }

    public void unsubscribeOHLC(ConversationContext conversationContext, Set<Pair> pairs) throws IOException {
        if (!connected) {
            return;
        }
        unsubscribeFrom(conversationContext, pairs);
        PingService.getInstance(session).stopAutoPinging();
        this.session.close();
    }

    private void subscribeTo(ConversationContext conversationContext, Set<Pair> pairs) throws IOException {
        logger.info("Subscribing OHLC-Data of {}", pairs.stream().map(Pair::technicalName).toList());
        this.session.getBasicRemote().sendText(buildSubscribeText(conversationContext, pairs));
    }

    private void connect(ConversationContext conversationContext) throws URISyntaxException, DeploymentException, IOException {
        logger.info("Trying connection to Websocket...");
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        webSocketContainer.connectToServer(this, new URI(conversationContext.streamingURL() + Constants.CONNECT));
    }

    private void unsubscribeFrom(ConversationContext conversationContext, Set<Pair> pairs) throws IOException {
        logger.info("Unsubscribing OHLC-Data of {}", pairs.stream().map(Pair::technicalName).toList());
        this.session.getBasicRemote().sendText(buildUnsubscribeText(conversationContext, pairs));
    }

    private String buildUnsubscribeText(ConversationContext conversationContext, Set<Pair> pairs) {
        return String.format("""
                        {
                            "destination": "OHLCMarketData.unsubscribe",
                            "correlationId": "%s",
                            "cst": "%s",
                            "securityToken": "%s",
                            "payload": {
                                "epics": [
                                    %s
                                ],
                                "resolutions": [
                                    %s
                                ],
                                "types": [
                                    "classic"
                                ]
                            }
                        }
                        """, UUID.randomUUID(),
                conversationContext.clientSecurityToken(), conversationContext.accountSecurityToken(),
                buildPairs(pairs), buildResolutions(pairs));
    }

    private String buildSubscribeText(ConversationContext conversationContext, Set<Pair> pairs) {
        return String.format("""
                        {
                                        "destination": "OHLCMarketData.subscribe",
                                        "correlationId": "%s",
                                        "cst": "%s",
                                        "securityToken": "%s",
                                        "payload": {
                                            "epics": [
                                                %s
                                            ],
                                            "resolutions": [
                                                %s
                                            ],
                                            "type": "classic"
                                        }
                                    }
                        """, UUID.randomUUID(),
                conversationContext.clientSecurityToken(), conversationContext.accountSecurityToken(),
                buildPairs(pairs), buildResolutions(pairs));
    }

    private String buildResolutions(Set<Pair> pairs) {
        PairConverter pairConverter = new PairConverter();
        return pairs.stream()
                .map(pairConverter::convertResolution)
                .distinct()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(","));
    }

    private String buildPairs(Set<Pair> pairs) {
        PairConverter pairConverter = new PairConverter();
        return pairs.stream()
                .map(pairConverter::convert)
                .distinct()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(","));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Websocket-Session closed. Reason: {}, Session: {}", closeReason, session);
        if (closeReason.getCloseCode().equals(CloseReason.CloseCodes.NORMAL_CLOSURE)) {
            return;
        }
        onClose.run();
    }

    @Override
    public void onError(Session session, Throwable thr) {
        logger.error("Websocket-Session encountered error. Error: {}, Session: {}", thr.getCause(), session, thr);
        onClose.run();
    }
}
