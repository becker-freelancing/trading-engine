package com.becker.freelance.backtest.resultviewer.app.callback;

import java.util.List;

public record ParsedBacktestResult(List<ParsedTrade> trades) {
}
