package com.becker.freelance.backtest.resultviewer.app.callback;

import com.becker.freelance.backtest.commons.BacktestResultContent;

import java.util.List;

public record ParsedBacktestResult(List<ParsedTrade> trades,
                                   BacktestResultContent resultContent) {
}
