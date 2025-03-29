package com.becker.freelance.tradeexecution.calculation;

import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.TrailingPosition;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.List;

public class PositionAdaptor {

    public List<Position> adapt(TimeSeriesEntry entry, List<Position> positions) {
        return new ArrayList<>(positions.stream().map(position -> adapt(entry, position)).toList());
    }

    private Position adapt(TimeSeriesEntry entry, Position position) {
        if (position instanceof TrailingPosition trailingPosition) {
            return switch (position.getDirection()) {
                case SELL -> adaptSell(entry, trailingPosition);
                case BUY -> adaptBuy(entry, trailingPosition);
            };
        }
        return position;
    }

    private Position adaptSell(TimeSeriesEntry entry, TrailingPosition position) {
        Decimal currentClose = entry.closeBid();
        Decimal stopDistance = position.initialStopLevel().subtract(position.getOpenPrice());
        Decimal currentStopDistance = position.getStopLevel().subtract(currentClose);

        if (currentStopDistance.isLessThan(stopDistance)) {
            return position;
        }

        Decimal adjustedStopLevel = currentClose.add(stopDistance);
        position.setStopLevel(adjustedStopLevel);
        return position;
    }

    private Position adaptBuy(TimeSeriesEntry entry, TrailingPosition position) {
        Decimal currentClose = entry.closeAsk();
        Decimal stopDistance = position.getOpenPrice().subtract(position.initialStopLevel());
        Decimal currentStopDistance = currentClose.subtract(position.getStopLevel());

        if (currentStopDistance.isLessThan(stopDistance)) {
            return position;
        }

        Decimal adjustedStopLevel = currentClose.subtract(stopDistance);
        position.setStopLevel(adjustedStopLevel);
        return position;
    }
}
