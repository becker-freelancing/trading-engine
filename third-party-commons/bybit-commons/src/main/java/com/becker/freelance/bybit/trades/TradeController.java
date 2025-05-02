package com.becker.freelance.bybit.trades;

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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TradeController {

    private final Logger logger = LoggerFactory.getLogger(TradeController.class);

    private final TradeApiClient apiClient;

    public TradeController() {
        apiClient = new TradeApiClient();
    }

    public List<Position> allPositions() {
        List<PositionResponse> allPositionsResponse;
        try {
            allPositionsResponse = apiClient.allPositions();
        } catch (URISyntaxException | IOException | InterruptedException e) {
            logger.error("Could not request all open Positions", e);
            return List.of();
        }

        return allPositionsResponse.stream().map(this::toPosition).toList();
    }

    private Position toPosition(PositionResponse positionResponse) {
        return new BybitPosition(positionResponse);
    }


    public Optional<Position> createPositionStopLimitLevel(Direction direction, Pair pair, Decimal size, Decimal stopLevel, Decimal limitLevel) {
        Optional<String> dealReference;
        try {
            dealReference = apiClient.createPositionStopLimitLevel(
                    direction,
                    pair,
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

    public List<Trade> closePositions(Pair pair, ExitSignal exitSignal) {
        Optional<Decimal> sizeToClose = allPositions().stream()
                .filter(position -> position.getDirection().equals(exitSignal.directionToClose()))
                .map(Position::getSize)
                .reduce(Decimal::add);

        if (sizeToClose.isEmpty()) {
            return List.of();
        }

        apiClient.marketOrder(pair, exitSignal.directionToClose().negate(), sizeToClose.get());


        return List.of();//TODO

    }

    private Optional<Position> getPosition(String dealReference) {
        return allPositions().stream()
                .filter(position -> position.getId().equals(dealReference))
                .findFirst();
    }

    public List<Trade> getTradesForDurationUntilNowForPair(Duration duration, Pair pair) {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minus(duration);
        return apiClient.getTradesInTime(from, to, pair)
                .toList();
    }
}
