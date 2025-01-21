package com.becker.freelance.tradeexecution;

import com.becker.freelance.commons.*;
import com.becker.freelance.commons.calculation.MarginCalculator;
import com.becker.freelance.commons.calculation.PositionCalculation;
import com.becker.freelance.commons.calculation.TradingCalculator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DemoTradeExecutor extends TradeExecutor {

    private Wallet wallet;
    private List<Position> openPositions;
    private List<Trade> closedTrades;
    private PositionCalculation positionCalculation;
    private TradingCalculator tradingCalculator;
    private Pair pair;

    public DemoTradeExecutor(){

    }

    private DemoTradeExecutor(ExecutionConfiguration executionConfiguration){
        wallet = new Wallet(executionConfiguration.getInitialWalletAmount());
        openPositions = new ArrayList<>();
        closedTrades = new ArrayList<>();
        pair = executionConfiguration.getPair();
        tradingCalculator = new TradingCalculator(pair, executionConfiguration.getEurUsdTimeSeries());
        MarginCalculator marginCalculator = new MarginCalculator(pair, executionConfiguration.getEurUsdTimeSeries());
        positionCalculation = new PositionCalculation(tradingCalculator, marginCalculator);
    }

    @Override
    protected TradeExecutor construct(ExecutionConfiguration executionConfiguration) {
        return new DemoTradeExecutor(executionConfiguration);
    }

    @Override
    protected boolean supports(AppMode appMode) {
        return AppMode.KRAKEN_DEMO.equals(appMode);
    }

    @Override
    public void closePositionsIfSlOrTpReached(TimeSeriesEntry currentPrice) {
        for (Position position : new ArrayList<>(openPositions)) {
            if (position.isTpReached(currentPrice) || position.isSlReached(currentPrice)) {
                TradingCalculator.ProfitLossResult profitConversionRate = position.currentProfit(currentPrice, tradingCalculator);
                closedTrades.add(positionCalculation.toTrade(profitConversionRate.umrechnungsFactor(), profitConversionRate.profit(), currentPrice.getPair(), position, currentPrice));
                wallet.adjustAmount(profitConversionRate.profit());
                wallet.removeMargin(position.getMargin());
                openPositions.remove(position);
            }
        }
    }

    @Override
    public void exit(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, ExitSignal exitSignal) {
        PositionCalculation.ClosePositionResults closePositionResults;
        if (exitSignal.getDirectionToClose() == Direction.BUY){
            closePositionResults = positionCalculation.closeAllBuyPositions(currentPrice, openPositions, currentPrice.getPair(), wallet);
        } else {
            closePositionResults = positionCalculation.closeAllSellPositions(currentPrice, openPositions, currentPrice.getPair(), wallet);
        }
        List<Trade> trades = closePositionResults.trades();
        openPositions = closePositionResults.positions();
        closedTrades.addAll(trades);
    }

    @Override
    public void entry(TimeSeriesEntry currentPrice, TimeSeries timeSeries, LocalDateTime time, EntrySignal entrySignal) {
        PositionCalculation.OpenPositionResult openPositionsResult = positionCalculation.openPosition(currentPrice, openPositions, entrySignal, wallet);
        openPositions = openPositionsResult.positions();
        closedTrades.addAll(openPositionsResult.trades());
    }

    @Override
    public List<Trade> getAllClosedTrades() {
        return closedTrades;
    }

    @Override
    public Wallet getWallet() {
        return wallet;
    }
}
