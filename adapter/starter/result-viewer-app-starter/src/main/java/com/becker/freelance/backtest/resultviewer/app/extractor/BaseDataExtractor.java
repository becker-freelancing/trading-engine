package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;

import java.util.List;

public class BaseDataExtractor implements ResultExtractor {

    private BacktestResultContent baseData = null;

    @Override
    public void consume(BacktestResultContent resultContent) {
        if (baseData == null) {
            baseData = resultContent;
        }
    }

    @Override
    public List<BacktestResultContent> getResult() {
        return List.of(baseData);
    }
}
