package com.becker.freelance.commons.calculation;

import com.becker.freelance.commons.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PositionCalculation {

    private TradingCalculator tradingCalculator;
    private MarginCalculator marginCalculator;

    public PositionCalculation(TradingCalculator tradingCalculator, MarginCalculator marginCalculator) {
        this.tradingCalculator = tradingCalculator;
        this.marginCalculator = marginCalculator;
    }

    public List<Position> toPosition(EntrySignal entrySignal, TimeSeriesEntry timeSeriesEntry,
                                     Wallet wallet) {
        Position position;
        if (entrySignal.getPositionType() == PositionType.HARD_LIMIT) {
            position = toHardLimitPosition(entrySignal, timeSeriesEntry);
        } else if (entrySignal.getPositionType() == PositionType.TRAILING) {
            position = toTrailingStopPosition(entrySignal, timeSeriesEntry);
        } else {
            throw new IllegalArgumentException("Provided illegal position type: " + entrySignal.getPositionType());
        }

        if (wallet.canOpen(position.getMargin())) {
            wallet.addMargin(position.getMargin());
            List<Position> positions = new ArrayList<>();
            positions.add(position);
            return positions;
        } else {
            return new ArrayList<>();
        }
    }

    public TrailingStopPosition toTrailingStopPosition(EntrySignal entrySignal, TimeSeriesEntry timeSeriesEntry) {
        double margin = marginCalculator.calcMargin(entrySignal.getAmount(), timeSeriesEntry);
        return new TrailingStopPosition(entrySignal.getAmount(), entrySignal.getDirection(),
                timeSeriesEntry, timeSeriesEntry.getPair(), entrySignal.getStopInPoints(),
                entrySignal.getLimitInPoints(), entrySignal.getTrailingStepSize(), margin);
    }

    public HardLimitPosition toHardLimitPosition(EntrySignal entrySignal, TimeSeriesEntry timeSeriesEntry) {
        double margin = marginCalculator.calcMargin(entrySignal.getAmount(), timeSeriesEntry);
        return new HardLimitPosition(entrySignal.getAmount(), entrySignal.getDirection(),
                timeSeriesEntry, timeSeriesEntry.getPair(), entrySignal.getStopInPoints(),
                entrySignal.getLimitInPoints(), margin);
    }

    public List<Trade> closeAllPositions(TimeSeriesEntry timeSeriesEntry, List<Position> positions,
                                         Pair pair, Wallet wallet) {
        List<Trade> trades = new ArrayList<>();
        for (Position position : positions) {
            wallet.removeMargin(position.getMargin());
            TradingCalculator.ProfitLossResult profitAndConversionRate = position.currentProfit(timeSeriesEntry, tradingCalculator);
            double currentProfit = profitAndConversionRate.profit();
            double conversionRate = profitAndConversionRate.umrechnungsFactor();
            wallet.adjustAmount(currentProfit);
            trades.add(toTrade(conversionRate, currentProfit, pair, position, timeSeriesEntry));
        }
        return trades;
    }


    public ClosePositionResults closeAllBuyPositions(TimeSeriesEntry timeSeriesEntry, List<Position> positions, Pair pair, Wallet wallet) {
        List<Position> positionsToClose = new ArrayList<>();
        List<Position> remainingPositions = new ArrayList<>();
        for (Position position : positions) {
            if (position.getDirection() == Direction.BUY){
                positionsToClose.add(position);
            } else {
                remainingPositions.add(position);
            }
        }

        List<Trade> trades = new ArrayList<>();
        for (Position position : positionsToClose) {
            wallet.removeMargin(position.getMargin());
            TradingCalculator.ProfitLossResult profitAndConversionRate = position.currentProfit(timeSeriesEntry, tradingCalculator);
            double currentProfit = profitAndConversionRate.profit();
            double conversionRate = profitAndConversionRate.umrechnungsFactor();
            wallet.adjustAmount(currentProfit);
            trades.add(toTrade(conversionRate, currentProfit, pair, position, timeSeriesEntry));
        }
        return new ClosePositionResults(remainingPositions, trades);
    }

    public ClosePositionResults closeAllSellPositions(TimeSeriesEntry timeSeriesEntry, List<Position> positions, Pair pair, Wallet wallet) {
        List<Position> positionsToClose = new ArrayList<>();
        List<Position> remainingPositions = new ArrayList<>();
        for (Position position : positions) {
            if (position.getDirection() == Direction.SELL){
                positionsToClose.add(position);
            } else {
                remainingPositions.add(position);
            }
        }

        List<Trade> trades = new ArrayList<>();
        for (Position position : positionsToClose) {
            wallet.removeMargin(position.getMargin());
            TradingCalculator.ProfitLossResult profitAndConversionRate = position.currentProfit(timeSeriesEntry, tradingCalculator);
            double currentProfit = profitAndConversionRate.profit();
            double conversionRate = profitAndConversionRate.umrechnungsFactor();
            wallet.adjustAmount(currentProfit);
            trades.add(toTrade(conversionRate, currentProfit, pair, position, timeSeriesEntry));
        }
        return new ClosePositionResults(remainingPositions, trades);
    }

    public Trade toTrade(double conversionRate, double currentProfit, Pair pair, Position position,
                         TimeSeriesEntry timeSeriesEntry) {
        return new Trade(position.getOpenTime(), timeSeriesEntry.getTime(), pair, currentProfit,
                position.getOpenPrice(), position.currentPrice(timeSeriesEntry), position.getSize(),
                position.getDirection(), conversionRate, position.getPositionType());
    }

    public OpenPositionResult openSinglePosition(TimeSeriesEntry timeSeriesEntry, EntrySignal entrySignal,
                                                 Wallet wallet) {
        List<Position> position = toPosition(entrySignal, timeSeriesEntry, wallet);
        return new OpenPositionResult(position, new ArrayList<>());
    }

    public OpenPositionResult openSameDirectionPositions(TimeSeriesEntry timeSeriesEntry, List<Position> positions,
                                                         EntrySignal entrySignal,
                                                         Wallet wallet) {
        List<Position> position = toPosition(entrySignal, timeSeriesEntry, wallet);
        positions.addAll(position);
        return new OpenPositionResult(positions, new ArrayList<>());
    }


    public static record UeberhangResult(Double sizeToCompensate, Position ueberhang, List<Position> positionsToClose){}

    public UeberhangResult getUeberhang(Double sizeToCompensate, List<Position> sortedPositions){
        List<Position> positionsToClose = new ArrayList<>();
        Position ueberhang = null;
        for (Position position: sortedPositions){
            double size = position.getSize();
            if (sizeToCompensate - size >= 0){
                sizeToCompensate -= size;
                positionsToClose.add(position);
            } else {
                ueberhang = position;
                break;
            }
        }
        return new UeberhangResult(sizeToCompensate, ueberhang, positionsToClose);
    }

    public OpenPositionResult openOtherDirectionPositions(TimeSeriesEntry timeSeriesEntry, List<Position> positions,
                                                          EntrySignal entrySignal,
                                                          Wallet wallet) {
        positions.sort(Comparator.comparing(Position::getOpenTime));

        UeberhangResult ueberhangResult = getUeberhang(Double.valueOf(entrySignal.getAmount()), positions);
        List<Position> positionsToClose = ueberhangResult.positionsToClose();
        Double sizeToCompensate = ueberhangResult.sizeToCompensate();

        List<Position> remainingOpenPositions = new ArrayList<>(positions.subList(positionsToClose.size(), positions.size()));

        compensateUeberhang(positionsToClose, remainingOpenPositions, sizeToCompensate, timeSeriesEntry, ueberhangResult.ueberhang(), wallet);

        List<Trade> closedPositions = closeAllPositions(timeSeriesEntry, positionsToClose, timeSeriesEntry.getPair(), wallet);

        return new OpenPositionResult(remainingOpenPositions, closedPositions);
    }

    public void compensateUeberhang(List<Position> positionsToClose, List<Position> remainingOpenPositions,
                                    double sizeToCompensate, TimeSeriesEntry timeSeriesEntry, Position ueberhang,
                                    Wallet wallet) {
        if (sizeToCompensate > 0) {
            double ueberhangClosingSize = ueberhang.getSize() - sizeToCompensate;
            double ueberhangRemainingSize = ueberhang.getSize() - ueberhangClosingSize;
            EntrySignal ueberhangEntrySignal = new EntrySignal(ueberhangRemainingSize, ueberhang.getDirection(),
                    ueberhang.getStopInPoints(), ueberhang.getLimitInPoints(), ueberhang.getPositionType(),
                    ueberhang instanceof TrailingStopPosition ? ((TrailingStopPosition) ueberhang).getTrailingStepSize() : null);

            List<Position> ueberhangRemainingPosition = toPosition(ueberhangEntrySignal, timeSeriesEntry, wallet);
            for (int i = ueberhangRemainingPosition.size() - 1; i >= 0; i--){
                remainingOpenPositions.add(0, ueberhangRemainingPosition.get(i));
            }

            EntrySignal ueberhangCloseEntrySignal = new EntrySignal(ueberhangRemainingSize, ueberhang.getDirection(),
                    ueberhang.getStopInPoints(), ueberhang.getLimitInPoints(), ueberhang.getPositionType(),
                    ueberhang instanceof TrailingStopPosition ? ((TrailingStopPosition) ueberhang).getTrailingStepSize() : null);

            List<Position> ueberhangClosePosition = toPosition(ueberhangCloseEntrySignal, timeSeriesEntry, wallet);
            positionsToClose.addAll(ueberhangClosePosition);
        }
    }

    public OpenPositionResult openPosition(TimeSeriesEntry timeSeriesEntry, List<Position> positions,
                                           EntrySignal entrySignal, Wallet wallet) {
        if (positions.isEmpty()) {
            return openSinglePosition(timeSeriesEntry, entrySignal, wallet);
        }

        Position openPosition = positions.get(0);
        if (openPosition.getDirection() == entrySignal.getDirection()) {
            return openSameDirectionPositions(timeSeriesEntry, positions, entrySignal, wallet);
        } else {
            return openOtherDirectionPositions(timeSeriesEntry, positions, entrySignal, wallet);
        }
    }

    public static record OpenPositionResult(List<Position> positions, List<Trade> trades){}

    public static record ClosePositionResults(List<Position> positions, List<Trade> trades){}

}
