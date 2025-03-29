package com.becker.freelance.tradeexecution.calculation.calculation;

import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.signal.AmountEntrySignal;
import com.becker.freelance.commons.signal.DistanceEntrySignal;
import com.becker.freelance.commons.signal.EntrySignalFactory;
import com.becker.freelance.commons.signal.LevelEntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.commons.wallet.Wallet;
import com.becker.freelance.math.Decimal;
import com.becker.freelance.tradeexecution.calculation.MarginCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation;
import com.becker.freelance.tradeexecution.calculation.PositionCalculation.PositionCalculationResult;
import com.becker.freelance.tradeexecution.calculation.TradingCalculatorImpl;
import com.becker.freelance.tradeexecution.calculation.mock.PairMock;
import com.becker.freelance.tradeexecution.position.DemoPositionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PositionCalculationTest {

    static final Decimal MARGIN_PER_POSITION = new Decimal("3330.00");

    static LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

    static LocalDateTime nextTime() {
        currentTime = currentTime.plusMinutes(1);
        return currentTime;
    }

    private TradingCalculator tradingCalculator;
    private MarginCalculator marginCalculator;
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
    private EntrySignalFactory entrySignalFactory;
    private DemoPositionFactory demoPositionFactory;
    private TimeSeriesEntry entryForMarginCalculation;

    @BeforeEach
    void setUp() {
        currentPrice = mock(TimeSeriesEntry.class);
        doReturn(PairMock.eurUsd()).when(currentPrice).pair();
        doReturn(new Decimal("10.")).when(currentPrice).getCloseMid();
        doReturn(new Decimal("10.")).when(currentPrice).closeAsk();
        doReturn(new Decimal("10.")).when(currentPrice).closeBid();
        doReturn(currentPrice.closeAsk()).when(currentPrice).lowAsk();
        doReturn(currentPrice.closeBid()).when(currentPrice).lowBid();
        doReturn(currentPrice.closeAsk()).when(currentPrice).highAsk();
        doReturn(currentPrice.closeBid()).when(currentPrice).highBid();
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
        doReturn(new Decimal("1.5")).when(entryForMarginCalculation).closeAsk();
        doReturn(new Decimal("1.5")).when(entryForMarginCalculation).closeBid();
        doCallRealMethod().when(entryForMarginCalculation).getCloseMid();
        tradingCalculator = new TradingCalculatorImpl(marginCalulationTimeSeries);
        marginCalculator = new MarginCalculatorImpl(marginCalulationTimeSeries);
        positionCalculation = new PositionCalculation(tradingCalculator, marginCalculator);

        entrySignalFactory = new EntrySignalFactory();
        demoPositionFactory = new DemoPositionFactory(marginCalulationTimeSeries);
        buyPosition = demoPositionFactory.createStopLimitPosition((LevelEntrySignal) entrySignalFactory.fromLevel(new Decimal("1"), Direction.BUY, new Decimal("5"), new Decimal("17"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        sellPosition = demoPositionFactory.createStopLimitPosition((LevelEntrySignal) entrySignalFactory.fromLevel(new Decimal("1"), Direction.SELL, new Decimal("15"), new Decimal("3"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        buyPosition2 = demoPositionFactory.createStopLimitPosition((LevelEntrySignal) entrySignalFactory.fromLevel(new Decimal("2"), Direction.BUY, new Decimal("5"), new Decimal("17"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        sellPosition2 = demoPositionFactory.createStopLimitPosition((LevelEntrySignal) entrySignalFactory.fromLevel(new Decimal("2"), Direction.SELL, new Decimal("15"), new Decimal("3"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        buyPosition3 = demoPositionFactory.createStopLimitPosition((LevelEntrySignal) entrySignalFactory.fromLevel(new Decimal("3"), Direction.BUY, new Decimal("5"), new Decimal("17"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        sellPosition3 = demoPositionFactory.createStopLimitPosition((LevelEntrySignal) entrySignalFactory.fromLevel(new Decimal("3"), Direction.SELL, new Decimal("15"), new Decimal("3"), PositionType.HARD_LIMIT, entryForMarginCalculation));

    }

    @Test
    void openSingleBuy() {
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, List.of(), buyPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(1, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSingleSell() {
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, List.of(), sellPosition, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(1, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(new Decimal("1"), position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSecondBuy() {
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

        assertEquals(MARGIN_PER_POSITION.multiply(new Decimal("3")), wallet.getMargin());
    }

    @Test
    void openSecondSell() {
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

        assertEquals(MARGIN_PER_POSITION.multiply(new Decimal("3")), wallet.getMargin());
    }

    @Test
    void openSellWithBiggerExistingBuy() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openBuyWithBiggerExistingSell() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSellWithSmallerExistingBuy() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openBuyWithSmallerExistingSell() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithSameExistingBuy() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleBiggerSell() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleSmallerBuy() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleSmallerSell() {
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

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleSameBuy() {
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
        Position position1 = demoPositionFactory.createStopLimitPosition((AmountEntrySignal) entrySignalFactory.fromAmount(new Decimal("1."), Direction.BUY, new Decimal("1"), new Decimal("1"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        Position position2 = demoPositionFactory.createStopLimitPosition((AmountEntrySignal) entrySignalFactory.fromAmount(new Decimal("1."), Direction.SELL, new Decimal("50000000"), new Decimal("500000000"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        Position position3 = demoPositionFactory.createStopLimitPosition((AmountEntrySignal) entrySignalFactory.fromAmount(new Decimal("1."), Direction.BUY, new Decimal("20"), new Decimal("20"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        wallet.addMargin(MARGIN_PER_POSITION.multiply(3));
        when(currentPrice.lowBid()).thenReturn(new Decimal("1.6"));
        when(currentPrice.lowAsk()).thenReturn(new Decimal("1.6"));
        when(currentPrice.highAsk()).thenReturn(new Decimal("1.6"));
        when(currentPrice.highBid()).thenReturn(new Decimal("1.6"));
        when(currentPrice.closeBid()).thenReturn(new Decimal("1.6"));
        when(currentPrice.closeAsk()).thenReturn(new Decimal("1.6"));

        PositionCalculationResult calculationResult = positionCalculation.closePositionIfSlOrTpReached(currentPrice, List.of(position1, position2, position3), wallet);

        assertEquals(2, calculationResult.trades().size());
        assertEquals(1, calculationResult.positions().size());
        assertEquals(new Decimal("10000014.00"), wallet.getAmount());
        assertEquals(new Decimal(MARGIN_PER_POSITION.multiply(1)), wallet.getMargin());
    }

    @Test
    void closeAllBuyPositions() {
        doReturn(Decimal.ZERO).when(entryForMarginCalculation).getCloseSpread();
        Position position1 = demoPositionFactory.createStopLimitPosition((DistanceEntrySignal) entrySignalFactory.fromDistance(new Decimal("1.0"), Direction.BUY, new Decimal("1"), new Decimal("1"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        Position position2 = demoPositionFactory.createStopLimitPosition((DistanceEntrySignal) entrySignalFactory.fromDistance(new Decimal("2.0"), Direction.SELL, new Decimal("1"), new Decimal("1"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        Position position3 = demoPositionFactory.createStopLimitPosition((DistanceEntrySignal) entrySignalFactory.fromDistance(new Decimal("3.0"), Direction.BUY, new Decimal("20"), new Decimal("20"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        wallet.addMargin(new Decimal(MARGIN_PER_POSITION.multiply(6)));

        PositionCalculationResult calculationResult = positionCalculation.closeAllBuyPositions(currentPrice, List.of(position1, position2, position3), wallet);

        assertEquals(2, calculationResult.trades().size());
        assertEquals(1, calculationResult.positions().size());
        assertEquals(new Decimal("32666666.67"), wallet.getAmount());
        assertEquals(new Decimal("6660.00"), wallet.getMargin());
    }

    @Test
    void closeAllSellPositions() {
        doReturn(Decimal.ZERO).when(entryForMarginCalculation).getCloseSpread();
        Position position1 = demoPositionFactory.createStopLimitPosition((DistanceEntrySignal) entrySignalFactory.fromDistance(new Decimal("1.0"), Direction.BUY, new Decimal("1"), new Decimal("1"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        Position position2 = demoPositionFactory.createStopLimitPosition((DistanceEntrySignal) entrySignalFactory.fromDistance(new Decimal("2.0"), Direction.SELL, new Decimal("1"), new Decimal("1"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        Position position3 = demoPositionFactory.createStopLimitPosition((DistanceEntrySignal) entrySignalFactory.fromDistance(new Decimal("3.0"), Direction.BUY, new Decimal("20"), new Decimal("20"), PositionType.HARD_LIMIT, entryForMarginCalculation));
        wallet.addMargin(MARGIN_PER_POSITION.multiply(6));

        PositionCalculationResult calculationResult = positionCalculation.closeAllSellPositions(currentPrice, List.of(position1, position2, position3), wallet);

        assertEquals(1, calculationResult.trades().size());
        assertEquals(2, calculationResult.positions().size());
        assertEquals(new Decimal("-1333333.33"), wallet.getAmount());
        assertEquals(MARGIN_PER_POSITION.multiply(4), wallet.getMargin());
    }
}
