package com.becker.freelance.commons.signal;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.calculation.TradingCalculator;
import com.becker.freelance.commons.calculation.TradingCalculatorImpl;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Direction;
import com.becker.freelance.commons.position.PositionType;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public interface EntrySignal {

    public Decimal getSize();

    public Direction getDirection();

    public Pair getPair();

    public TimeSeriesEntry getOpenPrice();

    public PositionType positionType();

    public default Decimal getOpenPriceForDirection() {
        return switch (getDirection()) {
            case SELL -> getOpenPrice().closeBid();
            case BUY -> getOpenPrice().closeAsk();
        };
    }

    public default LocalDateTime getOpenTime() {
        return getOpenPrice().time();
    }

    public void visit(EntrySignalVisitor visitor);

    public default LevelEntrySignal toLevelEntrySignal(EurUsdRequestor eurUsdRequestor) {
        TradingCalculator tradingCalculator = new TradingCalculatorImpl(eurUsdRequestor);
        return toLevelEntrySignal(tradingCalculator);
    }

    public default LevelEntrySignal toLevelEntrySignal(TradingCalculator tradingCalculator) {
        EntrySignalConverter entrySignalConverter = new EntrySignalConverter(tradingCalculator);
        visit(entrySignalConverter);
        return entrySignalConverter.getConvertion();
    }
}
