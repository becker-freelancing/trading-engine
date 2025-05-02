package com.becker.freelance.capital.env;

import com.becker.freelance.capital.authentication.AuthenticationApiClient;
import com.becker.freelance.capital.authentication.CreateSessionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationContextHolder {


    private static final Logger logger = LoggerFactory.getLogger(ConversationContextHolder.class);

    private static ConversationContext conversationContext;

    public static ConversationContext getConversationContext() {
        if (conversationContext == null) {
            ConversationContextHolder conversationContextHolder = new ConversationContextHolder();
            conversationContext = conversationContextHolder.load();
        }
        return conversationContext;
    }


    private ConversationContext load() {
        AuthenticationApiClient apiClient = new AuthenticationApiClient();
        CapitalUserConfig userConfig = new EnvironmentProvider().userConfig();

        return authenticate(
                apiClient,
                userConfig.login(),
                userConfig.password(),
                false, //TODO: Password Encryption
                userConfig.apiKey()
        );
    }

    private ConversationContext authenticate(AuthenticationApiClient authenticationService, String identifier, String password, boolean encryptedPassword, String apiKey) {
        logger.info("Authenticate...");
        CreateSessionRequest request = new CreateSessionRequest(identifier, password, encryptedPassword);
        ConversationContext session = authenticationService.createSession(request, apiKey);
        logger.info("Authentication successfully");
        return session;
    }

}
