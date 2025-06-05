package com.becker.freelance.tradeexecution.position;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingFeeCalculator;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.*;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;

import java.time.LocalDateTime;
import java.util.UUID;

public class DemoPositionFactory implements PositionFactory {

    private final EurUsdRequestor eurUsd;
    private final TradingFeeCalculator tradingFeeCalculator;

    public DemoPositionFactory(EurUsdRequestor eurUsd, TradingFeeCalculator tradingFeeCalculator) {
        this.eurUsd = eurUsd;
        this.tradingFeeCalculator = tradingFeeCalculator;
    }


    @Override
    public StopLimitPosition createStopLimitPosition(EntrySignal entrySignal) {
        return new StopLimitPositionImpl(entrySignal, PositionBehaviour.HARD_LIMIT, eurUsd, tradingFeeCalculator);
    }

    @Override
    public TrailingPosition createTrailingPosition(EntrySignal entrySignal) {
        return new TrailingPositionImpl(entrySignal, PositionBehaviour.TRAILING, eurUsd, tradingFeeCalculator);
    }


    private static class TrailingPositionImpl implements TrailingPosition {

        private final EntrySignal entrySignal;
        private final MarginCalculator marginCalculator;
        private final PositionBehaviour positionBehaviour;
        private final EurUsdRequestor eurUsd;
        private final String id;
        private Decimal size;
        private Decimal stopLevel;
        private final Decimal initialStopLevel;
        private final TradeableQuantilMarketRegime openMarketRegime;
        private final TradingFeeCalculator tradingFeeCalculator;

        public TrailingPositionImpl(EntrySignal entrySignal, PositionBehaviour positionBehaviour, EurUsdRequestor eurUsd, TradingFeeCalculator tradingFeeCalculator) {
            this(entrySignal, positionBehaviour, entrySignal.size(), eurUsd, entrySignal.stopLevel(), entrySignal.stopLevel(), entrySignal.openMarketRegime(), tradingFeeCalculator);
        }

        public TrailingPositionImpl(EntrySignal entrySignal, PositionBehaviour positionBehaviour, Decimal size, EurUsdRequestor eurUsd, Decimal stopLevel, Decimal initialStopLevel, TradeableQuantilMarketRegime openMarketRegime, TradingFeeCalculator tradingFeeCalculator) {
            this.entrySignal = entrySignal;
            this.positionBehaviour = positionBehaviour;
            this.size = size;
            this.marginCalculator = new MarginCalculatorImpl(eurUsd);
            this.eurUsd = eurUsd;
            this.stopLevel = stopLevel;
            this.initialStopLevel = initialStopLevel;
            this.tradingFeeCalculator = tradingFeeCalculator;
            this.id = UUID.randomUUID().toString();
            this.openMarketRegime = openMarketRegime;
        }

        @Override
        public Decimal getOpenFee() {
            return tradingFeeCalculator.calculateTradingFeeInCounterCurrency(entrySignal, size);
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
            return entrySignal.direction();
        }

        @Override
        public Pair getPair() {
            return entrySignal.pair();
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
            return marginCalculator.getMarginEur(getPair(), getSize(), getOpenPrice(), getOpenTime()).divide(getLeverage());
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
        public PositionBehaviour getPositionType() {
            return positionBehaviour;
        }

        @Override
        public boolean isOpenTaker() {
            return entrySignal.isOpenTaker();
        }

        @Override
        public boolean isCloseTaker() {
            return entrySignal.isCloseTaker();
        }

        @Override
        public TradeableQuantilMarketRegime getOpenMarketRegime() {
            return openMarketRegime;
        }

        @Override
        public Position clone() {
            return new TrailingPositionImpl(
                    entrySignal,
                    positionBehaviour,
                    size,
                    eurUsd,
                    stopLevel,
                    initialStopLevel,
                    openMarketRegime,
                    tradingFeeCalculator
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

        private final EntrySignal entrySignal;
        private final MarginCalculator marginCalculator;
        private final PositionBehaviour positionBehaviour;
        private final EurUsdRequestor eurUsd;
        private final String id;
        private final TradeableQuantilMarketRegime openMarketRegime;
        private final TradingFeeCalculator tradingFeeCalculator;
        private Decimal size;

        public StopLimitPositionImpl(EntrySignal entrySignal, PositionBehaviour positionBehaviour, EurUsdRequestor eurUsd, TradingFeeCalculator tradingFeeCalculator) {
            this(entrySignal, positionBehaviour, entrySignal.size(), eurUsd, entrySignal.openMarketRegime(), tradingFeeCalculator);
        }

        public StopLimitPositionImpl(EntrySignal entrySignal, PositionBehaviour positionBehaviour, Decimal size, EurUsdRequestor eurUsd, TradeableQuantilMarketRegime openMarketRegime, TradingFeeCalculator tradingFeeCalculator) {
            this.entrySignal = entrySignal;
            this.positionBehaviour = positionBehaviour;
            this.size = size;
            this.marginCalculator = new MarginCalculatorImpl(eurUsd);
            this.eurUsd = eurUsd;
            this.tradingFeeCalculator = tradingFeeCalculator;
            this.id = UUID.randomUUID().toString();
            this.openMarketRegime = openMarketRegime;
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
            return entrySignal.direction();
        }

        @Override
        public Pair getPair() {
            return entrySignal.pair();
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
            return marginCalculator.getMarginEur(getPair(), getSize(), getOpenPrice(), getOpenTime()).divide(getLeverage());
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
        public Decimal getOpenFee() {
            return tradingFeeCalculator.calculateTradingFeeInCounterCurrency(entrySignal, getSize());
        }

        @Override
        public PositionBehaviour getPositionType() {
            return positionBehaviour;
        }

        @Override
        public boolean isOpenTaker() {
            return entrySignal.isOpenTaker();
        }

        @Override
        public boolean isCloseTaker() {
            return entrySignal.isCloseTaker();
        }

        @Override
        public Position clone() {
            return new StopLimitPositionImpl(
                    entrySignal,
                    positionBehaviour,
                    size,
                    eurUsd,
                    openMarketRegime,
                    tradingFeeCalculator
            );
        }

        @Override
        public TradeableQuantilMarketRegime getOpenMarketRegime() {
            return openMarketRegime;
        }

        @Override
        public String getId() {
            return id;
        }
    }
}
