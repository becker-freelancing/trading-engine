package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

public class EntrySignalConverter implements EntrySignalVisitor {


    private final TradingCalculator tradingCalculator;
    private LevelEntrySignal convert;

    public EntrySignalConverter(TradingCalculator tradingCalculator) {
        this.tradingCalculator = tradingCalculator;
    }


    @Override
    public void accept(AmountEntrySignal entrySignal) {
        convert = convert(entrySignal);
    }

    @Override
    public void accept(DistanceEntrySignal entrySignal) {
        convert = convert(entrySignal);
    }

    @Override
    public void accept(LevelEntrySignal entrySignal) {
        convert = convert(entrySignal);
    }

    public LevelEntrySignal convert(LevelEntrySignal entrySignal) {
        return entrySignal;
    }

    public LevelEntrySignal convert(DistanceEntrySignal entrySignal) {
        return convert(entrySignal, entrySignal.limitDistance(), entrySignal.stopDistance());
    }

    public LevelEntrySignal convert(AmountEntrySignal entrySignal) {
        Decimal limitDistance = tradingCalculator.getDistanceByAmount(entrySignal.pair(), entrySignal.size(), entrySignal.limitAmount());
        Decimal stopDistance = tradingCalculator.getDistanceByAmount(entrySignal.pair(), entrySignal.size(), entrySignal.stopAmount());
        return convert(entrySignal, limitDistance, stopDistance);
    }

    public LevelEntrySignal getConvertion() {
        return convert;
    }

    private LevelEntrySignal convert(EntrySignal entrySignal, Decimal limitDistance, Decimal stopDistance) {
        Decimal closeSpread = entrySignal.openPrice().getCloseSpread();
        Decimal stopLevel = getStopLevel(closeSpread, stopDistance, entrySignal.getOpenPriceForDirection(), entrySignal.direction());
        Decimal limitLevel = getLimitLevel(closeSpread, limitDistance, entrySignal.getOpenPriceForDirection(), entrySignal.direction());

        return new LevelEntrySignalImpl(entrySignal, stopLevel, limitLevel, entrySignal.positionType());
    }

    private Decimal getLimitLevel(Decimal closeSpread, Decimal limitDistance, Decimal openPriceForDirection, Direction direction) {
        return switch (direction) {
            case BUY -> openPriceForDirection.add(closeSpread).add(limitDistance);
            case SELL -> openPriceForDirection.subtract(closeSpread).subtract(limitDistance);
        };
    }

    private Decimal getStopLevel(Decimal closeSpread, Decimal stopDistance, Decimal openPriceForDirection, Direction direction) {
        return switch (direction) {
            case BUY -> openPriceForDirection.subtract(stopDistance).add(closeSpread);
            case SELL -> openPriceForDirection.add(stopDistance).subtract(closeSpread);
        };
    }

    private record LevelEntrySignalImpl(EntrySignal entrySignal, Decimal stopLevel, Decimal limitLevel,
                                        PositionType positionType) implements LevelEntrySignal {

        @Override
        public Decimal size() {
            return entrySignal.size();
        }

        @Override
        public Direction direction() {
            return entrySignal.direction();
        }

        @Override
        public Pair pair() {
            return entrySignal.pair();
        }

        @Override
        public TimeSeriesEntry openPrice() {
            return entrySignal.openPrice();
        }

        @Override
        public TradeableQuantilMarketRegime openMarketRegime() {
            return entrySignal.openMarketRegime();
        }
    }
}
