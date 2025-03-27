package com.becker.freelance.capital;


import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.ConversationContextHolder;
import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Test {

    public static void main(String[] args) throws URISyntaxException, DeploymentException, IOException {
        WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
        System.out.println(webSocketContainer);

        ConversationContext conversationContext = ConversationContextHolder.getConversationContext();

        Endpoint endpoint = new Endpoint();

        webSocketContainer.connectToServer(endpoint, new URI("wss://api-streaming-capital.backend-capital.com/connect"));
        endpoint.subscribe(conversationContext.clientSecurityToken(), conversationContext.accountSecurityToken());

        new Thread(() -> {
            try {
                System.out.println("Sleep");
                Thread.sleep(100000000000000L);
                System.out.println("Awake");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).run();
    }

    static class Endpoint extends jakarta.websocket.Endpoint {

        private Session session;

        @Override
        public void onOpen(Session session, EndpointConfig endpointConfig) {
            this.session = session;
            System.out.println("on Open " + session);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    System.out.println("Message: " + s);
                }
            });
        }

        public void subscribe(String cst, String securityToken) throws IOException {
            this.session.getBasicRemote().sendText(
                    String.format("""
                            {
                                "destination": "OHLCMarketData.subscribe",
                                "correlationId": "3",
                                "cst": "%s",
                                "securityToken": "%s",
                                "payload": {
                                    "epics": [
                                        "BTCEUR",
                                        "AAPL"
                                    ],
                                    "resolutions": [
                                        "MINUTE"
                                    ],
                                    "type": "classic"
                                }
                            }
                            """, cst, securityToken)
            );
        }
    }
}
