package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.List;

public class BestMinExtractor implements ResultExtractor {

    private Decimal bestMinValue = Decimal.MINUS_DOUBLE_MAX;
    private List<BacktestResultContent> bestResults = new ArrayList<>();

    @Override
    public void consume(BacktestResultContent resultContent) {
        Decimal min = resultContent.min();
        if (min.isGreaterThan(bestMinValue)) {
            bestMinValue = min;
            bestResults = new ArrayList<>();
        }
        if (min.isEqualTo(bestMinValue)) {
            bestResults.add(resultContent);
        }
    }

    @Override
    public List<BacktestResultContent> getResult() {
        return bestResults;
    }
}
