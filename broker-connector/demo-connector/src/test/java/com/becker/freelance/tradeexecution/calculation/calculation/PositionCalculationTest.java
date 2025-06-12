package com.becker.freelance.tradeexecution.calculation.calculation;

import com.becker.freelance.commons.calculation.*;
import com.becker.freelance.commons.order.OrderBuilder;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionBehaviour;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.signal.EntrySignalBuilder;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation.PositionCalculationResult;
import com.becker.freelance.tradeexecution.calculation.mock.PairMock;
import com.becker.freelance.tradeexecution.position.DemoPositionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PositionCalculationTest {

    static final Decimal MARGIN_PER_POSITION = new Decimal("475.71");

    static LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

    static LocalDateTime nextTime() {
        currentTime = currentTime.plusMinutes(1);
        return currentTime;
    }

    private TradingCalculator tradingCalculator;
    private PositionCalculation positionCalculation;
    private TimeSeriesEntry currentPrice;
    private TimeSeriesEntry otherPrice;
    private Wallet wallet;
    private Position buyPosition;
    private Position buyPosition2;
    private Position buyPosition3;
    private Position sellPosition;
    private Position sellPosition2;
    private Position sellPosition3;
    private DemoPositionFactory demoPositionFactory;
    private TimeSeriesEntry entryForMarginCalculation;

    @BeforeEach
    void setUp() {
        currentPrice = mock(TimeSeriesEntry.class);
        doReturn(PairMock.eurUsd()).when(currentPrice).pair();
        doReturn(new Decimal("10.")).when(currentPrice).getOpenMid();
        doReturn(new Decimal("10.")).when(currentPrice).getCloseMid();
        doReturn(new Decimal("10.")).when(currentPrice).closeAsk();
        doReturn(new Decimal("10.")).when(currentPrice).closeBid();
        doReturn(currentPrice.closeAsk()).when(currentPrice).lowAsk();
        doReturn(currentPrice.closeBid()).when(currentPrice).lowBid();
        doReturn(currentPrice.closeAsk()).when(currentPrice).highAsk();
        doReturn(currentPrice.closeBid()).when(currentPrice).highBid();
        doCallRealMethod().when(currentPrice).getClosePriceForDirection(any());
        doCallRealMethod().when(currentPrice).getHighPriceForDirection(any());
        doCallRealMethod().when(currentPrice).getLowPriceForDirection(any());
        doReturn(nextTime()).when(currentPrice).time();
        otherPrice = mock(TimeSeriesEntry.class);
        doReturn(PairMock.eurUsd()).when(otherPrice).pair();
        doReturn(new Decimal("2.")).when(otherPrice).getCloseMid();
        doReturn(new Decimal("2.")).when(otherPrice).closeAsk();
        doReturn(new Decimal("2.")).when(otherPrice).closeBid();
        doReturn(nextTime()).when(otherPrice).time();
        wallet = new WalletMock(new Decimal("10000000"));

        TimeSeries marginCalulationTimeSeries = mock(TimeSeries.class);
        entryForMarginCalculation = mock(TimeSeriesEntry.class);
        doReturn(entryForMarginCalculation).when(marginCalulationTimeSeries).getEntryForTime(any());
        doReturn(LocalDateTime.now()).when(entryForMarginCalculation).time();
        doReturn(PairMock.eurUsd()).when(entryForMarginCalculation).pair();
        doReturn(PairMock.eurUsd()).when(marginCalulationTimeSeries).getPair();
        doReturn(new Decimal("7")).when(entryForMarginCalculation).closeAsk();
        doReturn(new Decimal("7")).when(entryForMarginCalculation).closeBid();
        doCallRealMethod().when(entryForMarginCalculation).getCloseMid();
        doCallRealMethod().when(entryForMarginCalculation).getClosePriceForDirection(any());
        EurUsdRequestor eurUsdRequestor = time -> entryForMarginCalculation;
        tradingCalculator = new TradingCalculatorImpl(eurUsdRequestor);
        MarginCalculator marginCalculator = new MarginCalculatorImpl(eurUsdRequestor);
        TradingFeeCalculator tradingFeeCalculator = mock(TradingFeeCalculator.class);
        doReturn(new Decimal(2)).when(tradingFeeCalculator).calculateTakerTradingFeeInCounterCurrency(any(), any());
        doReturn(new Decimal(2)).when(tradingFeeCalculator).calculateMakerTradingFeeInCounterCurrency(any(), any());
        doCallRealMethod().when(tradingFeeCalculator).calculateTradingFeeInCounterCurrency(any());
        doCallRealMethod().when(tradingFeeCalculator).calculateTradingFeeInCounterCurrency(any(), any());
        positionCalculation = new PositionCalculation(tradingCalculator, tradingFeeCalculator);

        demoPositionFactory = new DemoPositionFactory(eurUsdRequestor, tradingFeeCalculator, marginCalculator);
        buyPosition = demoPositionFactory.createStopLimitPosition(new EntrySignalBuilder()
                .withOpenOrder(OrderBuilder.getInstance().withPair(PairMock.eurUsd()).withDirection(Direction.BUY).withSize(Decimal.ONE).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("3"))).withThresholdPrice(new Decimal("3")))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("17")))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .build(currentTime));

        buyPosition2 = demoPositionFactory.createStopLimitPosition(new EntrySignalBuilder()
                .withOpenOrder(OrderBuilder.getInstance().withPair(PairMock.eurUsd()).withDirection(Direction.BUY).withSize(Decimal.TWO).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("15")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("5"))).withThresholdPrice(new Decimal("5")))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .build(currentTime));


        buyPosition3 = demoPositionFactory.createStopLimitPosition(new EntrySignalBuilder()
                .withOpenOrder(OrderBuilder.getInstance().withPair(PairMock.eurUsd()).withDirection(Direction.BUY).withSize(new Decimal("3")).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("17"))).withThresholdPrice(new Decimal("17")))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("5")))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .build(currentTime));


        sellPosition = demoPositionFactory.createStopLimitPosition(new EntrySignalBuilder()
                .withOpenOrder(OrderBuilder.getInstance().withPair(PairMock.eurUsd()).withDirection(Direction.SELL).withSize(Decimal.ONE).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("15"))).withThresholdPrice(new Decimal("15")))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("3")))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .build(currentTime));


        sellPosition2 = demoPositionFactory.createStopLimitPosition(new EntrySignalBuilder()
                .withOpenOrder(OrderBuilder.getInstance().withPair(PairMock.eurUsd()).withDirection(Direction.SELL).withSize(Decimal.TWO).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("3"))).withThresholdPrice(new Decimal("3")))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("15")))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .build(currentTime));


        sellPosition3 = demoPositionFactory.createStopLimitPosition(new EntrySignalBuilder()
                .withOpenOrder(OrderBuilder.getInstance().withPair(PairMock.eurUsd()).withDirection(Direction.SELL).withSize(new Decimal("3")).asMarketOrder())
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("15"))).withThresholdPrice(new Decimal("15")))
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("3")))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .build(currentTime));

    }

    @Test
    void openSingleBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(1, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSingleSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(1, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSecondBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);

        assertEquals(2, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());
        Position position2 = positions.get(1);
        assertEquals(Direction.BUY, position2.getDirection());
        assertEquals(new Decimal("2"), position2.getSize());

        assertMargin(MARGIN_PER_POSITION.multiply(new Decimal("3")), wallet.getMargin());
    }

    @Test
    void openSecondSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(2, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());
        Position position2 = positions.get(1);
        assertEquals(Direction.SELL, position2.getDirection());
        assertEquals(new Decimal("2"), position2.getSize());

        assertMargin(MARGIN_PER_POSITION.multiply(new Decimal("3")), wallet.getMargin());
    }

    @Test
    void openSellWithBiggerExistingBuy() {
        buyPosition2.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openBuyWithBiggerExistingSell() {
        sellPosition2.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSellWithSmallerExistingBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openBuyWithSmallerExistingSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithSameExistingBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());

        assertEquals(0, positions.size());

        assertEquals(new Decimal("0.00"), wallet.getMargin());
    }


    @Test
    void openBuyWithSameExistingSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());

        assertEquals(0, positions.size());

        assertEquals(new Decimal("0.00"), wallet.getMargin());
    }


    @Test
    void openSellWithMultipleBiggerBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition2.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), sellPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(new Decimal("1"), trade.getSize());
        Trade trade2 = trades.get(1);
        assertEquals(Direction.BUY, trade2.getDirection());
        assertEquals(new Decimal("1"), trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleBiggerSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition2.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), buyPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(new Decimal("1"), trade.getSize());
        Trade trade2 = trades.get(1);
        assertEquals(Direction.SELL, trade2.getDirection());
        assertEquals(new Decimal("1"), trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleSmallerBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition3.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), sellPosition3, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(new Decimal("1"), trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.BUY, trade2.getDirection());
        assertEquals(new Decimal("1"), trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleSmallerSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition3.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition, wallet);
        wallet.adjustAmount(Decimal.DOUBLE_MAX);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), buyPosition3, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(new Decimal("1"), trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.SELL, trade2.getDirection());
        assertEquals(new Decimal("1"), trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertMargin(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleSameBuy() {
        buyPosition.getOpenOrder().executeIfPossible(currentPrice);
        sellPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), buyPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), sellPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(new Decimal("1"), trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.BUY, trade2.getDirection());
        assertEquals(new Decimal("1"), trade2.getSize());

        assertEquals(0, positions.size());

        assertEquals(new Decimal("0.00"), wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleSameSell() {
        sellPosition.getOpenOrder().executeIfPossible(currentPrice);
        buyPosition2.getOpenOrder().executeIfPossible(currentPrice);
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), sellPosition, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), buyPosition2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(new Decimal("1"), trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.SELL, trade2.getDirection());
        assertEquals(new Decimal("1"), trade2.getSize());

        assertEquals(0, positions.size());

        assertEquals(new Decimal("0.00"), wallet.getMargin());
    }

    @Test
    void closePositionIfSlOrTpReached() {
        doReturn(Decimal.ZERO).when(entryForMarginCalculation).getCloseSpread();

        Pair pair = PairMock.eurUsd();
        Position position1 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.BUY).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.4")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1"))).withThresholdPrice(new Decimal("1")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        Position position2 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.SELL).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("0.1")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("100"))).withThresholdPrice(new Decimal("100")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        Position position3 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.BUY).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.5")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.2"))).withThresholdPrice(new Decimal("1.2")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        position1.getOpenOrder().executeIfPossible(entryForMarginCalculation);
        position2.getOpenOrder().executeIfPossible(entryForMarginCalculation);
        position3.getOpenOrder().executeIfPossible(entryForMarginCalculation);

        Decimal marginPerPosition = new Decimal("333.00");

        wallet.addMargin(marginPerPosition.multiply(3));
        when(currentPrice.openBid()).thenReturn(new Decimal("1.6"));
        when(currentPrice.openAsk()).thenReturn(new Decimal("1.6"));
        when(currentPrice.lowBid()).thenReturn(new Decimal("1.6"));
        when(currentPrice.lowAsk()).thenReturn(new Decimal("1.6"));
        doCallRealMethod().when(currentPrice).getLowMid();
        when(currentPrice.highAsk()).thenReturn(new Decimal("1.6"));
        when(currentPrice.highBid()).thenReturn(new Decimal("1.6"));
        doCallRealMethod().when(currentPrice).getHighMid();
        when(currentPrice.closeBid()).thenReturn(new Decimal("1.6"));
        when(currentPrice.closeAsk()).thenReturn(new Decimal("1.6"));

        PositionCalculationResult calculationResult = positionCalculation.closePositionIfSlOrTpReached(currentPrice, List.of(position1, position2, position3), wallet);

        assertEquals(2, calculationResult.trades().size());
        assertEquals(1, calculationResult.positions().size());
        assertEquals(new Decimal("8414277.71"), wallet.getAmount());
        assertEquals(new Decimal(marginPerPosition.multiply(1)), wallet.getMargin());
    }

    @Test
    void closeAllBuyPositions() {
        doReturn(Decimal.ZERO).when(entryForMarginCalculation).getCloseSpread();
        Pair pair = PairMock.eurUsd();

        Position position1 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.BUY).withPair(pair).asMarketOrder().asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.4")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1"))).withThresholdPrice(new Decimal("1")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        Position position2 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(new Decimal("2")).withDirection(Direction.SELL).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("0.1")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("100"))).withThresholdPrice(new Decimal("100")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        Position position3 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(new Decimal("3")).withDirection(Direction.BUY).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.5")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.2"))).withThresholdPrice(new Decimal("1.2")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        position1.getOpenOrder().executeIfPossible(entryForMarginCalculation);
        position2.getOpenOrder().executeIfPossible(entryForMarginCalculation);
        position3.getOpenOrder().executeIfPossible(entryForMarginCalculation);

        Decimal marginPerPosition = new Decimal("333.00");

        wallet.addMargin(new Decimal(marginPerPosition.multiply(6)));

        PositionCalculationResult calculationResult = positionCalculation.closeAllBuyPositions(currentPrice, List.of(position1, position2, position3), wallet);

        assertEquals(2, calculationResult.trades().size());
        assertEquals(1, calculationResult.positions().size());
        assertEquals(new Decimal("11714277.72"), wallet.getAmount());
        assertMargin(marginPerPosition.multiply(2), wallet.getMargin());
    }

    @Test
    void closeAllSellPositions() {
        doReturn(Decimal.ZERO).when(entryForMarginCalculation).getCloseSpread();
        Pair pair = PairMock.eurUsd();

        Position position1 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(Decimal.ONE).withDirection(Direction.BUY).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.4")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1"))).withThresholdPrice(new Decimal("1")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        Position position2 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(new Decimal("2")).withDirection(Direction.SELL).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("0.1")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("100"))).withThresholdPrice(new Decimal("100")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));

        Position position3 = demoPositionFactory.createStopLimitPosition(EntrySignalBuilder.getInstance()
                .withOpenOrder(OrderBuilder.getInstance().withSize(new Decimal("3")).withDirection(Direction.BUY).withPair(pair).asMarketOrder())
                .withLimitOrder(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.5")))
                .withStopOrder(OrderBuilder.getInstance().asConditionalOrder().withDelegate(OrderBuilder.getInstance().asLimitOrder().withOrderPrice(new Decimal("1.2"))).withThresholdPrice(new Decimal("1.2")))
                .withOpenMarketRegime(mock(TradeableQuantilMarketRegime.class))
                .withPositionBehaviour(PositionBehaviour.HARD_LIMIT)
                .build(currentTime));


        position1.getOpenOrder().executeIfPossible(entryForMarginCalculation);
        position2.getOpenOrder().executeIfPossible(entryForMarginCalculation);
        position3.getOpenOrder().executeIfPossible(entryForMarginCalculation);

        Decimal marginPerPosition = new Decimal("333.00");
        wallet.addMargin(marginPerPosition.multiply(6));

        PositionCalculationResult calculationResult = positionCalculation.closeAllSellPositions(currentPrice, List.of(position1, position2, position3), wallet);

        assertEquals(1, calculationResult.trades().size());
        assertEquals(2, calculationResult.positions().size());
        assertEquals(new Decimal("9142853.14"), wallet.getAmount());
        assertMargin(marginPerPosition.multiply(4), wallet.getMargin());
    }

    void assertMargin(Decimal expected, Decimal actual) {
        assertEquals(expected.round(1), actual.round(1));
    }
}
