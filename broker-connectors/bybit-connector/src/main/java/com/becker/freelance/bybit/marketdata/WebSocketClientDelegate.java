package com.becker.freelance.bybit.marketdata;

import com.bybit.api.client.websocket.WebSocketHttpClientSingleton;
import com.bybit.api.client.websocket.WebsocketClient;
import com.bybit.api.client.websocket.WebsocketMessageHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.bybit.api.client.constant.Util.generateTransferID;

class WebSocketClientDelegate implements WebsocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketClientDelegate.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final WebsocketMessageHandler messageHandler;
    private final WebSocketHttpClientSingleton webSocketHttpClientSingleton;

    private final String baseUrl;
    private final Boolean debugMode;
    private final Runnable onError;
    private List<String> argNames;
    private String path;
    private CompletableFuture<WebSocket> ws = new CompletableFuture<>();

    public WebSocketClientDelegate(String baseUrl, Boolean debugMode, WebsocketMessageHandler messageHandler, Runnable onError) {
        this.messageHandler = messageHandler;
        this.baseUrl = baseUrl;
        this.debugMode = debugMode;
        this.onError = onError;
        webSocketHttpClientSingleton = WebSocketHttpClientSingleton.createInstance(this.debugMode, "okhttp3");
    }

    private void setupPublicChannelStream(List<String> argNames, String path) {
        this.argNames = new ArrayList<>(argNames);
        this.path = path;
    }

    private void sendJsonMessage(WebSocket ws, Object messageObject, String messageType) {
        try {
            String json = objectMapper.writeValueAsString(messageObject);
            ws.send(json);
            LOGGER.info("Sent {}: {}", messageType, json);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing {} message: ", messageType, e);
        }
    }

    private void sendSubscribeMessage(WebSocket ws) {
        Map<String, Object> subscribeMsg = createSubscribeMessage();
        sendJsonMessage(ws, subscribeMsg, "Subscribe");
    }

    @NotNull
    private Map<String, Object> createSubscribeMessage() {
        Map<String, Object> subscribeMsg = new LinkedHashMap<>();
        subscribeMsg.put("op", "subscribe");
        subscribeMsg.put("req_id", generateTransferID());
        subscribeMsg.put("args", argNames);
        return subscribeMsg;
    }

    @NotNull
    private String getWssUrl() {
        return baseUrl + path;
    }

    @NotNull
    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                WebSocketClientDelegate.this.onClose(code, reason);
            }

            @Override
            public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                WebSocketClientDelegate.this.onError(t);
            }

            @Override
            public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                WebSocketClientDelegate.this.onMessage(text);
            }

            @Override
            public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                WebSocketClientDelegate.this.onOpen(webSocket);
            }
        };
    }

    @Override
    public void onMessage(String msg) {
        if (messageHandler != null) {
            messageHandler.handleMessage(msg);
        } else {
            LOGGER.info(msg);
        }
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.error("Error in Web-Socket-Client", t);
        onError.run();

        try {
            PingService.getInstance(ws.get()).stopAutoPinging();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Could not stop Auto-Ping", e);
        }
    }

    @Override
    public void onClose(int code, String reason) {
        try {
            PingService.getInstance(ws.get()).stopAutoPinging();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Could not stop Auto-Ping", e);
        }
        LOGGER.warn("websocket connection is about to close: {} - {}", code, reason);
    }

    @Override
    public void onOpen(WebSocket ws) {

        // If no authentication is needed, just send the subscribed message.
        sendSubscribeMessage(ws);

        this.ws.complete(ws);
        PingService.getInstance(ws).startAutoPinging();
    }

    @Override
    public void connect() {
        String wssUrl = getWssUrl();
        LOGGER.info("Connecting Websocket to {}", wssUrl);
        webSocketHttpClientSingleton.createWebSocket(wssUrl, createWebSocketListener());
    }

    @Override
    public void getPublicChannelStream(List<String> argNames, String path) {
        setupPublicChannelStream(argNames, path);
        connect();
    }

    @Override
    public void getPrivateChannelStream(List<String> argNames, String path) {
        setupPublicChannelStream(argNames, path);
        connect();
    }

    public void disconnect() {
        try {
            ws.get().close(1001, null);
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Could not disconnect from websocket", e);
        }
    }
}
