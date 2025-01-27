package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.calculation.PositionCalculation2.PositionCalculationResult;
import com.becker.freelance.commons.mock.PairMock;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.commons.signal.Direction;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.wallet.Wallet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PositionCalculationTest {

    static final double MARGIN_PER_POSITION = 2220.0;

    static LocalDateTime currentTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);

    static LocalDateTime nextTime() {
        currentTime = currentTime.plusMinutes(1);
        return currentTime;
    }

    private TradingCalculator tradingCalculator;
    private MarginCalculator marginCalculator;
    private PositionCalculation2 positionCalculation;
    private TimeSeriesEntry currentPrice;
    private Wallet wallet;
    private EntrySignal buyEntrySignal;
    private EntrySignal buyEntrySignal2;
    private EntrySignal buyEntrySignal3;
    private EntrySignal sellEntrySignal;
    private EntrySignal sellEntrySignal2;
    private EntrySignal sellEntrySignal3;

    @BeforeEach
    void setUp() {
        currentPrice = mock(TimeSeriesEntry.class);
        doReturn(PairMock.eurUsd()).when(currentPrice).pair();
        doReturn(1.).when(currentPrice).getCloseMid();
        doReturn(nextTime()).when(currentPrice).time();
        wallet = new Wallet(10000);
        buyEntrySignal = new EntrySignal(1, Direction.BUY, 5, 7, PositionType.HARD_LIMIT);
        sellEntrySignal = new EntrySignal(1, Direction.SELL, 5, 7, PositionType.HARD_LIMIT);
        buyEntrySignal2 = new EntrySignal(2, Direction.BUY, 5, 7, PositionType.HARD_LIMIT);
        sellEntrySignal2 = new EntrySignal(2, Direction.SELL, 5, 7, PositionType.HARD_LIMIT);
        buyEntrySignal3 = new EntrySignal(3, Direction.BUY, 5, 7, PositionType.HARD_LIMIT);
        sellEntrySignal3 = new EntrySignal(3, Direction.SELL, 5, 7, PositionType.HARD_LIMIT);
        tradingCalculator = mock(TradingCalculator.class);
        doReturn(new TradingCalculator.ProfitLossResult(1., 1.)).when(tradingCalculator).calcProfitLoss(anyDouble(), anyDouble(), any(), any(), anyDouble());
        TimeSeries marginCalulationTimeSeries = mock(TimeSeries.class);
        TimeSeriesEntry entryForMarginCalculation = mock(TimeSeriesEntry.class);
        doReturn(entryForMarginCalculation).when(marginCalulationTimeSeries).getEntryForTime(any());
        doReturn(Pair.eurUsd1()).when(marginCalulationTimeSeries).getPair();
        doReturn(1.5).when(entryForMarginCalculation).getCloseMid();
        marginCalculator = new MarginCalculator(Pair.eurUsd1(), marginCalulationTimeSeries);
        positionCalculation = new PositionCalculation2(tradingCalculator, marginCalculator);
    }

    @Test
    void openSingleBuy() {
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(1, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSingleSell() {
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(1, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSecondBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);

        assertEquals(2, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(1, position.getSize());
        Position position2 = positions.get(1);
        assertEquals(Direction.BUY, position2.getDirection());
        assertEquals(2, position2.getSize());

        assertEquals(3 * MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSecondSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(List.of(), trades);
        assertEquals(2, positions.size());

        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(1, position.getSize());
        Position position2 = positions.get(1);
        assertEquals(Direction.SELL, position2.getDirection());
        assertEquals(2, position2.getSize());

        assertEquals(3 * MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSellWithBiggerExistingBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openBuyWithBiggerExistingSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openSellWithSmallerExistingBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }

    @Test
    void openBuyWithSmallerExistingSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithSameExistingBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());

        assertEquals(0, positions.size());

        assertEquals(0, wallet.getMargin());
    }


    @Test
    void openBuyWithSameExistingSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(1, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());

        assertEquals(0, positions.size());

        assertEquals(0, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleBiggerBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), sellEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(1, trade.getSize());
        Trade trade2 = trades.get(1);
        assertEquals(Direction.BUY, trade2.getDirection());
        assertEquals(1, trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleBiggerSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal2, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), buyEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(1, trade.getSize());
        Trade trade2 = trades.get(1);
        assertEquals(Direction.SELL, trade2.getDirection());
        assertEquals(1, trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleSmallerBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), sellEntrySignal3, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(1, trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.BUY, trade2.getDirection());
        assertEquals(1, trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.SELL, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleSmallerSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), buyEntrySignal3, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(1, trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.SELL, trade2.getDirection());
        assertEquals(1, trade2.getSize());

        assertEquals(1, positions.size());
        Position position = positions.get(0);
        assertEquals(Direction.BUY, position.getDirection());
        assertEquals(1, position.getSize());

        assertEquals(MARGIN_PER_POSITION, wallet.getMargin());
    }


    @Test
    void openSellWithMultipleSameBuy() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), buyEntrySignal, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), buyEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), sellEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.BUY, trade.getDirection());
        assertEquals(1, trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.BUY, trade2.getDirection());
        assertEquals(1, trade2.getSize());

        assertEquals(0, positions.size());

        assertEquals(0, wallet.getMargin());
    }


    @Test
    void openBuyWithMultipleSameSell() {
        PositionCalculationResult preResult = positionCalculation.openPosition(currentPrice, List.of(), sellEntrySignal, wallet);
        PositionCalculationResult preResult2 = positionCalculation.openPosition(currentPrice, preResult.positions(), sellEntrySignal, wallet);
        PositionCalculationResult positionCalculationResult = positionCalculation.openPosition(currentPrice, preResult2.positions(), buyEntrySignal2, wallet);

        List<Position> positions = positionCalculationResult.positions();
        List<Trade> trades = positionCalculationResult.trades();

        assertEquals(2, trades.size());
        Trade trade = trades.get(0);
        assertEquals(Direction.SELL, trade.getDirection());
        assertEquals(1, trade.getSize());
        Trade trade2 = trades.get(0);
        assertEquals(Direction.SELL, trade2.getDirection());
        assertEquals(1, trade2.getSize());

        assertEquals(0, positions.size());

        assertEquals(0, wallet.getMargin());
    }
}
