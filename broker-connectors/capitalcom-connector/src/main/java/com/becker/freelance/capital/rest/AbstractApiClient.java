package com.becker.freelance.capital.rest;

import com.becker.freelance.capital.env.ConversationContext;
import com.becker.freelance.capital.env.EnvironmentProvider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public abstract class AbstractApiClient {

    protected HttpClient httpClient;
    private String baseUrl;

    public AbstractApiClient(String baseUrl) {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = new EnvironmentProvider().baseURL();
    }

    protected HttpRequest.Builder buildHttpRequest(ConversationContext context, String endpoint, String method, String requestBody) {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");

        if (context != null) {
            requestBuilder.header("Cap-App-Key", context.apiKey());
            requestBuilder.header("Client-Sso-Token", context.clientSecurityToken());
            requestBuilder.header("Account-Sso-Token", context.accountSecurityToken());
        }

        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
            requestBuilder.method(method, HttpRequest.BodyPublishers.ofString(requestBody));
        } else {
            requestBuilder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        return requestBuilder;
    }

    public String getApiDomainURL() {
        return baseUrl;
    }
}