package com.becker.freelance.capital.trades;

import com.becker.freelance.capital.env.EnvironmentProvider;
import com.becker.freelance.capital.util.Constants;
import com.becker.freelance.capital.util.HttpRequestBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class TradeApiClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient;
    private final String baseUrl;

    public TradeApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.baseUrl = new EnvironmentProvider().baseURL();
    }

    public AllPositionsResponse allPositions() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequestBuilder.builder()
                .uri(new URI(baseUrl.concat(Constants.API_V1_POSITIONS)))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        AllPositionsResponse allPositionsResponse = objectMapper.readValue(response.body(), AllPositionsResponse.class);
        return allPositionsResponse;
    }


}