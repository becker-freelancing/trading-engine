package com.becker.freelance.capital.util;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.ConversationContextHolder;

import java.net.http.HttpRequest;

public class HttpRequestBuilder {

    private HttpRequestBuilder() {
    }

    public static HttpRequest.Builder builder() {
        ConversationContext conversationContext = ConversationContextHolder.getConversationContext();
        return HttpRequest.newBuilder()
                .header("X-SECURITY-TOKEN", conversationContext.accountSecurityToken())
                .header("CST", conversationContext.clientSecurityToken());
    }
}
