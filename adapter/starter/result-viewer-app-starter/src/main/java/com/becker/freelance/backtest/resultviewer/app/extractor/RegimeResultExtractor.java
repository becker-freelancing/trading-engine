package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.commons.regime.TradeableMarketRegime;

import java.util.List;
import java.util.Map;

public interface RegimeResultExtractor extends ResultExtractor {

    Map<TradeableMarketRegime, List<BacktestResultContent>> getResultByRegime();

    @Override
    default List<BacktestResultContent> getResult() {
        throw new UnsupportedOperationException("Not supported");
    }
}
