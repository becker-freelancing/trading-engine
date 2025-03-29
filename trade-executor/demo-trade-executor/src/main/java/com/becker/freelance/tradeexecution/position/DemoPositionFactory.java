package com.becker.freelance.tradeexecution.position;

import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.*;
import com.becker.freelance.commons.signal.AmountEntrySignal;
import com.becker.freelance.commons.signal.DistanceEntrySignal;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.TradingCalculatorImpl;

import java.time.LocalDateTime;

public class DemoPositionFactory implements PositionFactory {

    private final TradingCalculator tradingCalculator;
    private final TimeSeries eurUsd;

    public DemoPositionFactory(TimeSeries eurUsd) {
        tradingCalculator = new TradingCalculatorImpl(eurUsd);
        this.eurUsd = eurUsd;
    }


    @Override
    public StopLimitPosition createStopLimitPosition(LevelEntrySignal entrySignal) {
        return new StopLimitPositionImpl(entrySignal, PositionType.HARD_LIMIT, eurUsd);
    }

    @Override
    public TrailingPosition createTrailingPosition(LevelEntrySignal entrySignal) {
        return new TrailingPositionImpl(entrySignal, PositionType.TRAILING, eurUsd);
    }

    @Override
    public StopLimitPosition createStopLimitPosition(DistanceEntrySignal entrySignal) {
        return createStopLimitPosition(convert(entrySignal));
    }

    @Override
    public TrailingPosition createTrailingPosition(DistanceEntrySignal entrySignal) {
        return createTrailingPosition(convert(entrySignal));
    }

    @Override
    public StopLimitPosition createStopLimitPosition(AmountEntrySignal entrySignal) {
        return createStopLimitPosition(convert(entrySignal));
    }

    @Override
    public TrailingPosition createTrailingPosition(AmountEntrySignal entrySignal) {
        return createTrailingPosition(convert(entrySignal));
    }

    private LevelEntrySignal convert(AmountEntrySignal entrySignal) {
        Decimal limitDistance = tradingCalculator.getDistanceByAmount(entrySignal.getPair(), entrySignal.getSize(), entrySignal.getLimitAmount());
        Decimal stopDistance = tradingCalculator.getDistanceByAmount(entrySignal.getPair(), entrySignal.getSize(), entrySignal.getStopAmount());
        return convert(entrySignal, limitDistance, stopDistance);
    }

    private LevelEntrySignal convert(DistanceEntrySignal entrySignal) {

        return convert(entrySignal, entrySignal.getLimitDistance(), entrySignal.getStopDistance());
    }

    private LevelEntrySignal convert(EntrySignal entrySignal, Decimal limitDistance, Decimal stopDistance) {
        Decimal closeSpread = entrySignal.getOpenPrice().getCloseSpread();
        Decimal stopLevel = getStopLevel(closeSpread, stopDistance, entrySignal.getOpenPriceForDirection(), entrySignal.getDirection());
        Decimal limitLevel = getLimitLevel(closeSpread, limitDistance, entrySignal.getOpenPriceForDirection(), entrySignal.getDirection());

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

    private static class LevelEntrySignalImpl implements LevelEntrySignal {

        private final EntrySignal entrySignal;
        private final Decimal stopLevel;
        private final Decimal limitLevel;
        private final PositionType positionType;

        public LevelEntrySignalImpl(EntrySignal entrySignal, Decimal stopLevel, Decimal limitLevel, PositionType positionType) {
            this.entrySignal = entrySignal;
            this.stopLevel = stopLevel;
            this.limitLevel = limitLevel;
            this.positionType = positionType;
        }

        @Override
        public Decimal getStopLevel() {
            return stopLevel;
        }

        @Override
        public Decimal getLimitLevel() {
            return limitLevel;
        }

        @Override
        public Decimal getSize() {
            return entrySignal.getSize();
        }

        @Override
        public Direction getDirection() {
            return entrySignal.getDirection();
        }

        @Override
        public Pair getPair() {
            return entrySignal.getPair();
        }

        @Override
        public TimeSeriesEntry getOpenPrice() {
            return entrySignal.getOpenPrice();
        }

        @Override
        public PositionType positionType() {
            return positionType;
        }
    }

    private static class TrailingPositionImpl implements TrailingPosition {

        private final LevelEntrySignal entrySignal;
        private final MarginCalculator marginCalculator;
        private final PositionType positionType;
        private final TimeSeries eurUsd;
        private Decimal size;
        private Decimal stopLevel;
        private Decimal initialStopLevel;

        public TrailingPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, TimeSeries eurUsd) {
            this(entrySignal, positionType, entrySignal.getSize(), eurUsd, entrySignal.getStopLevel(), entrySignal.getStopLevel());
        }

        public TrailingPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, Decimal size, TimeSeries eurUsd, Decimal stopLevel, Decimal initialStopLevel) {
            this.entrySignal = entrySignal;
            this.positionType = positionType;
            this.size = size;
            this.marginCalculator = new MarginCalculatorImpl(eurUsd);
            this.eurUsd = eurUsd;
            this.stopLevel = stopLevel;
            this.initialStopLevel = initialStopLevel;
        }

        @Override
        public Decimal getSize() {
            return size;
        }

        @Override
        public void setSize(Decimal size) {
            this.size = size;
        }

        @Override
        public Direction getDirection() {
            return entrySignal.getDirection();
        }

        @Override
        public Pair getPair() {
            return entrySignal.getPair();
        }

        @Override
        public Decimal getOpenPrice() {
            return entrySignal.getOpenPriceForDirection();
        }

        @Override
        public LocalDateTime getOpenTime() {
            return entrySignal.getOpenTime();
        }

        @Override
        public Decimal getMargin() {
            return marginCalculator.getMarginEur(getPair(), getSize(), getOpenPrice(), getOpenTime());
        }

        @Override
        public Decimal getStopLevel() {
            return stopLevel;
        }

        @Override
        public void setStopLevel(Decimal level) {
            this.stopLevel = level;
        }

        @Override
        public Decimal getLimitLevel() {
            return entrySignal.getLimitLevel();
        }

        @Override
        public PositionType getPositionType() {
            return positionType;
        }

        @Override
        public Position clone() {
            return new TrailingPositionImpl(
                    entrySignal,
                    positionType,
                    size,
                    eurUsd,
                    stopLevel,
                    initialStopLevel
            );
        }

        @Override
        public Decimal initialStopLevel() {
            return initialStopLevel;
        }
    }

    private static class StopLimitPositionImpl implements StopLimitPosition {

        private final LevelEntrySignal entrySignal;
        private final MarginCalculator marginCalculator;
        private final PositionType positionType;
        private final TimeSeries eurUsd;
        private Decimal size;

        public StopLimitPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, TimeSeries eurUsd) {
            this(entrySignal, positionType, entrySignal.getSize(), eurUsd);
        }

        public StopLimitPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, Decimal size, TimeSeries eurUsd) {
            this.entrySignal = entrySignal;
            this.positionType = positionType;
            this.size = size;
            this.marginCalculator = new MarginCalculatorImpl(eurUsd);
            this.eurUsd = eurUsd;
        }

        @Override
        public Decimal getSize() {
            return size;
        }

        @Override
        public void setSize(Decimal size) {
            this.size = size;
        }

        @Override
        public Direction getDirection() {
            return entrySignal.getDirection();
        }

        @Override
        public Pair getPair() {
            return entrySignal.getPair();
        }

        @Override
        public Decimal getOpenPrice() {
            return entrySignal.getOpenPriceForDirection();
        }

        @Override
        public LocalDateTime getOpenTime() {
            return entrySignal.getOpenTime();
        }

        @Override
        public Decimal getMargin() {
            return marginCalculator.getMarginEur(getPair(), getSize(), getOpenPrice(), getOpenTime());
        }

        @Override
        public Decimal getStopLevel() {
            return entrySignal.getStopLevel();
        }

        @Override
        public Decimal getLimitLevel() {
            return entrySignal.getLimitLevel();
        }

        @Override
        public PositionType getPositionType() {
            return positionType;
        }

        @Override
        public Position clone() {
            return new StopLimitPositionImpl(
                    entrySignal,
                    positionType,
                    size,
                    eurUsd
            );
        }
    }
}
