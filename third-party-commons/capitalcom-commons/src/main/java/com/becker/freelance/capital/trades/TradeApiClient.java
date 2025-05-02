package com.becker.freelance.capital.trades;

import com.becker.freelance.capital.env.EnvironmentProvider;
import com.becker.freelance.capital.util.Constants;
import com.becker.freelance.capital.util.HttpRequestBuilder;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

class TradeApiClient {

    private final static Logger logger = LoggerFactory.getLogger(TradeApiClient.class);

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

        return objectMapper.readValue(response.body(), AllPositionsResponse.class);
    }


    public Optional<String> createPositionStopLimitLevel(Direction direction, String convert, Decimal size, Decimal stopLevel, Decimal limitLevel) throws IOException, InterruptedException, URISyntaxException {
        CreatePositionRequest createPositionRequest = new CreatePositionStopLimitLevelRequest(convert, map(direction), size.doubleValue(), stopLevel.doubleValue(), limitLevel.doubleValue());

        return executeOpenPositionRequest(createPositionRequest);
    }

    private Optional<String> executeOpenPositionRequest(CreatePositionRequest createPositionRequest) throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequestBuilder.builder()
                .header("Content-Type", "application/json")
                .uri(new URI(baseUrl.concat(Constants.API_V1_POSITIONS)))
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(createPositionRequest)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            logger.warn("Could not open position. Reason: {}", response.body());
            return Optional.empty();
        }

        CreatePositionResponse createPositionResponse = objectMapper.readValue(response.body(), CreatePositionResponse.class);
        return Optional.ofNullable(createPositionResponse.getDealReference());
    }


    public Optional<String> createPositionStopLimitAmount(Direction direction, String convert, Decimal size, Decimal stopAmount, Decimal limitAmount) throws URISyntaxException, IOException, InterruptedException {
        CreatePositionRequest createPositionRequest = new CreatePositionStopLimitAmountRequest(convert, map(direction), size.doubleValue(), stopAmount.doubleValue(), limitAmount.doubleValue());

        return executeOpenPositionRequest(createPositionRequest);
    }

    public Optional<String> createPositionStopLimitDistance(Direction direction, String convert, Decimal size, Decimal stopDistance, Decimal limitDistance) throws URISyntaxException, IOException, InterruptedException {
        CreatePositionRequest createPositionRequest = new CreatePositionStopLimitDistanceRequest(convert, map(direction), size.doubleValue(), stopDistance.doubleValue(), limitDistance.doubleValue());

        return executeOpenPositionRequest(createPositionRequest);
    }

    private String map(Direction direction) {
        return switch (direction) {
            case BUY -> "BUY";
            case SELL -> "SELL";
        };
    }


    public boolean closePosition(Position position) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequestBuilder.builder()
                .uri(new URI(baseUrl.concat(Constants.API_V1_POSITIONS).concat("/").concat(position.getId())))
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.statusCode() == 200;
    }
}