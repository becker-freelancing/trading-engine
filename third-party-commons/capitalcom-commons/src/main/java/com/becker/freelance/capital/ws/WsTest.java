package com.becker.freelance.capital.ws;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.ConversationContextHolder;

import java.util.List;

public class WsTest {

    public static void main(String[] args) throws Exception {
        ConversationContext conversationContext = ConversationContextHolder.getConversationContext();

        WsClient wsClient = new WsClient();
        wsClient.connect(conversationContext);

        wsClient.subscribeOHLCMarketData(List.of("BTCUSD"), List.of("MINUTE"), bar -> {
            System.out.println(bar);
        });
    }
}
