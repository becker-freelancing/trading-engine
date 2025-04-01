package com.becker.freelance.tradeexecution.position;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.*;
import com.becker.freelance.commons.signal.AmountEntrySignal;
import com.becker.freelance.commons.signal.DistanceEntrySignal;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.util.calculation.TradingCalculatorImpl;
import com.becker.freelance.tradeexecution.util.signal.EntrySignalConverter;

import java.time.LocalDateTime;
import java.util.UUID;

public class DemoPositionFactory implements PositionFactory {

    private final EurUsdRequestor eurUsd;
    private final EntrySignalConverter signalConverter;

    public DemoPositionFactory(EurUsdRequestor eurUsd) {
        TradingCalculator tradingCalculator = new TradingCalculatorImpl(eurUsd);
        this.eurUsd = eurUsd;
        this.signalConverter = new EntrySignalConverter(tradingCalculator);
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
        LevelEntrySignal convert = signalConverter.convert(entrySignal);
        return createStopLimitPosition(convert);
    }

    @Override
    public TrailingPosition createTrailingPosition(DistanceEntrySignal entrySignal) {
        LevelEntrySignal convert = signalConverter.convert(entrySignal);
        return createTrailingPosition(convert);
    }

    @Override
    public StopLimitPosition createStopLimitPosition(AmountEntrySignal entrySignal) {
        LevelEntrySignal convert = signalConverter.convert(entrySignal);
        return createStopLimitPosition(convert);
    }

    @Override
    public TrailingPosition createTrailingPosition(AmountEntrySignal entrySignal) {
        LevelEntrySignal convert = signalConverter.convert(entrySignal);
        return createTrailingPosition(convert);
    }

    private static class TrailingPositionImpl implements TrailingPosition {

        private final LevelEntrySignal entrySignal;
        private final MarginCalculator marginCalculator;
        private final PositionType positionType;
        private final EurUsdRequestor eurUsd;
        private final String id;
        private Decimal size;
        private Decimal stopLevel;
        private final Decimal initialStopLevel;

        public TrailingPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, EurUsdRequestor eurUsd) {
            this(entrySignal, positionType, entrySignal.getSize(), eurUsd, entrySignal.stopLevel(), entrySignal.stopLevel());
        }

        public TrailingPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, Decimal size, EurUsdRequestor eurUsd, Decimal stopLevel, Decimal initialStopLevel) {
            this.entrySignal = entrySignal;
            this.positionType = positionType;
            this.size = size;
            this.marginCalculator = new MarginCalculatorImpl(eurUsd);
            this.eurUsd = eurUsd;
            this.stopLevel = stopLevel;
            this.initialStopLevel = initialStopLevel;
            this.id = UUID.randomUUID().toString();
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
            return entrySignal.limitLevel();
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
        public String getId() {
            return id;
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
        private final EurUsdRequestor eurUsd;
        private final String id;
        private Decimal size;

        public StopLimitPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, EurUsdRequestor eurUsd) {
            this(entrySignal, positionType, entrySignal.getSize(), eurUsd);
        }

        public StopLimitPositionImpl(LevelEntrySignal entrySignal, PositionType positionType, Decimal size, EurUsdRequestor eurUsd) {
            this.entrySignal = entrySignal;
            this.positionType = positionType;
            this.size = size;
            this.marginCalculator = new MarginCalculatorImpl(eurUsd);
            this.eurUsd = eurUsd;
            this.id = UUID.randomUUID().toString();
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
            return entrySignal.stopLevel();
        }

        @Override
        public Decimal getLimitLevel() {
            return entrySignal.limitLevel();
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

        @Override
        public String getId() {
            return id;
        }
    }
}
