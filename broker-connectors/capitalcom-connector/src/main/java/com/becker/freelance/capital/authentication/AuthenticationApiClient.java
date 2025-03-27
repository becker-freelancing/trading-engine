package com.becker.freelance.capital.authentication;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.EnvironmentProvider;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.crypto.Cipher;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.util.Base64;

public class AuthenticationApiClient {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String PKCS1_PADDING_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final String baseUrl;

    public AuthenticationApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = new EnvironmentProvider().baseURL();
    }

    public ConversationContext createSession(CreateSessionRequest request, String apiKey) {
        try {
            return createSessionInternal(request, apiKey);
        } catch (Exception e) {
            throw new IllegalStateException("Could not create Session", e);
        }
    }

    private ConversationContext createSessionInternal(CreateSessionRequest request, String apiKey) throws Exception {
        ConversationContext conversationContext = new ConversationContext(apiKey, null, null, null);

        if (request.encryptPassword()) {
            GetEncryptionKeySessionResponse encryptionKeyResponse = encryptionKeyResponse(conversationContext);
            String encryptedPassword = encryptPassword(encryptionKeyResponse.encryptionKey(),
                    encryptionKeyResponse.timeStamp(),
                    request.getPassword());
            request.setPassword(encryptedPassword);
        }

        String requestBody = objectMapper.writeValueAsString(request);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "api/v1/session"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .header("X-CAP-API-KEY", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        CreateSessionResponse sessionResponse = objectMapper.readValue(response.body(), CreateSessionResponse.class);

        return new ConversationContext(apiKey,
                response.headers().firstValue("cst").orElse(null),
                response.headers().firstValue("x-security-token").orElse(null),
                sessionResponse.getStreamingHost());
    }

    public String encryptPassword(String encryptionKey, Long timestamp, String password) {
        try {
            String inputString = password + "|" + timestamp;
            byte[] input = Base64.getEncoder().encode(inputString.getBytes(StandardCharsets.UTF_8));

            KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(encryptionKey)));

            Cipher cipher = Cipher.getInstance(PKCS1_PADDING_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(input);

            return Base64.getEncoder().encodeToString(output);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private GetEncryptionKeySessionResponse encryptionKeyResponse(ConversationContext conversationContext) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/v1/session/encryption-key"))
                .header("Authorization", "Bearer " + conversationContext.apiKey())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readValue(response.body(), GetEncryptionKeySessionResponse.class);
    }
}