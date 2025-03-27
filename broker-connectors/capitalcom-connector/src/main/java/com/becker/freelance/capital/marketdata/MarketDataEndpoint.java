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
import java.util.function.Consumer;

class MarketDataEndpoint extends Endpoint {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataEndpoint.class);
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
        onOpen.run();
    }

    public void subscribeOHLC(ConversationContext conversationContext, Pair pair) throws IOException, URISyntaxException, DeploymentException {
        if (!connected) {
            connect(conversationContext);
        }
        this.session.getBasicRemote().sendText(buildSubscribeText(conversationContext, pair));
        PingService.getInstance(session).startAutoPinging();
    }

    private void connect(ConversationContext conversationContext) throws URISyntaxException, DeploymentException, IOException {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        webSocketContainer.connectToServer(this, new URI(conversationContext.streamingURL() + Constants.CONNECT));
    }

    private String buildSubscribeText(ConversationContext conversationContext, Pair pair) {
        PairConverter pairConverter = new PairConverter();
        return String.format("""
                        {
                                        "destination": "OHLCMarketData.subscribe",
                                        "correlationId": "3",
                                        "cst": "%s",
                                        "securityToken": "%s",
                                        "payload": {
                                            "epics": [
                                                "%s"
                                            ],
                                            "resolutions": [
                                                "%s"
                                            ],
                                            "type": "classic"
                                        }
                                    }
                        """,
                conversationContext.clientSecurityToken(), conversationContext.accountSecurityToken(),
                pairConverter.convert(pair), pairConverter.convertResolution(pair));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        logger.info("Websocket-Session closed. Reason: {}, Session: {}", closeReason, session);
        onClose.run();
    }

    @Override
    public void onError(Session session, Throwable thr) {
        logger.error("Websocket-Session encountered error. Error: {}, Session: {}", thr.getCause(), session, thr);
        onClose.run();
    }
}
