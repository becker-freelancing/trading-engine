package com.becker.freelance.capital.trades;

import com.becker.freelance.capital.util.PairConverter;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.signal.ExitSignal;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

public class TradeController {

    private final Logger logger = LoggerFactory.getLogger(TradeController.class);

    private final TradeApiClient apiClient;

    public TradeController() {
        apiClient = new TradeApiClient();
    }

    public List<Position> allPositions() {
        AllPositionsResponse allPositionsResponse;
        try {
            allPositionsResponse = apiClient.allPositions();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error("Could not request all open Positions", e);
            return List.of();
        }

        return allPositionsResponse.getPositions().stream().map(this::toPosition).toList();
    }

    private Position toPosition(PositionItem positionItem) {
        MarketData market = positionItem.getMarket();
        Pair pair = toPair(market);
        PositionResponse position = positionItem.getPosition();
        return new CapitalPosition(pair, position);
    }

    private Pair toPair(MarketData market) {
        PairConverter pairConverter = new PairConverter();
        return pairConverter.convert(market.getEpic(), "MINUTE")
                .orElseThrow(() -> new IllegalArgumentException("Could not convert " + market.getEpic() + " to Pair"));
    }

    public Optional<Position> createPositionStopLimitLevel(Direction direction, Pair pair, Decimal size, Decimal stopLevel, Decimal limitLevel) {
        PairConverter pairConverter = new PairConverter();
        Optional<String> dealReference;
        try {
            dealReference = apiClient.createPositionStopLimitLevel(
                    direction,
                    pairConverter.convert(pair),
                    size,
                    stopLevel,
                    limitLevel
            );
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Could not create position", e);
            return Optional.empty();
        }

        return dealReference.flatMap(this::getPosition);
    }

    public Optional<Position> createPositionStopLimitDistance(Direction direction, Pair pair, Decimal size, Decimal stopDistance, Decimal limitDistance) {
        PairConverter pairConverter = new PairConverter();
        Optional<String> dealReference;
        try {
            dealReference = apiClient.createPositionStopLimitDistance(
                    direction,
                    pairConverter.convert(pair),
                    size,
                    stopDistance,
                    limitDistance
            );
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Could not create position", e);
            return Optional.empty();
        }

        return dealReference.flatMap(this::getPosition);
    }

    public Optional<Position> createPositionStopLimitAmount(Direction direction, Pair pair, Decimal size, Decimal stopAmount, Decimal limitAmount) {
        PairConverter pairConverter = new PairConverter();
        Optional<String> dealReference;
        try {
            dealReference = apiClient.createPositionStopLimitAmount(
                    direction,
                    pairConverter.convert(pair),
                    size,
                    stopAmount,
                    limitAmount
            );
        } catch (IOException | InterruptedException | URISyntaxException e) {
            logger.error("Could not create position", e);
            return Optional.empty();
        }

        return dealReference.flatMap(this::getPosition);
    }

    public List<Trade> closePositions(ExitSignal exitSignal) {
        List<Position> positionsToClose = allPositions().stream()
                .filter(position -> position.getDirection().equals(exitSignal.directionToClose()))
                .toList();

        for (Position position : positionsToClose) {
            try {
                apiClient.closePosition(position);
            } catch (IOException | InterruptedException | URISyntaxException e) {
                logger.error("Could not close position", e);
            }
        }

        return List.of();//TODO

    }

    private Optional<Position> getPosition(String dealReference) {
        return allPositions().stream()
                .filter(position -> position.getId().equals(dealReference))
                .findFirst();
    }

}
