package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;

import java.util.ArrayList;
import java.util.List;

public class MostTradesExtractor implements ResultExtractor {

    private int mostTradesValue = -1;
    private List<BacktestResultContent> bestResults = new ArrayList<>();

    @Override
    public void consume(BacktestResultContent resultContent) {
        int min = resultContent.tradeObjects().size();
        if (min > mostTradesValue) {
            mostTradesValue = min;
            bestResults = new ArrayList<>();
        }
        if (min == mostTradesValue) {
            bestResults.add(resultContent);
        }
    }

    @Override
    public List<BacktestResultContent> getResult() {
        return bestResults;
    }
}
