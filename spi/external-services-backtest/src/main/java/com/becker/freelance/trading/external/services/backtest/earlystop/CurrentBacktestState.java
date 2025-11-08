package com.becker.freelance.trading.external.services.backtest.earlystop;

import com.becker.freelance.commons.trade.Trade;

import java.util.List;

public interface CurrentBacktestState {

    public List<Trade> currentExecutedTrades();

}
