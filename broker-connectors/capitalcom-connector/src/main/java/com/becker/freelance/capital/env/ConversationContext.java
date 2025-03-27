package com.becker.freelance.capital.env;

public record ConversationContext(
        String apiKey,
        String clientSecurityToken,
        String accountSecurityToken,
        String streamingURL) {
}
