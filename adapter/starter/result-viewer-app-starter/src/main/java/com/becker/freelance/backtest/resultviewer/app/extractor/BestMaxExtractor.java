package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.List;

public class BestMaxExtractor implements ResultExtractor {

    private Decimal bestMaxValue = Decimal.MINUS_DOUBLE_MAX;
    private List<BacktestResultContent> bestResults = new ArrayList<>();

    @Override
    public void consume(BacktestResultContent resultContent) {
        Decimal max = resultContent.max();
        if (max.isGreaterThan(bestMaxValue)) {
            bestMaxValue = max;
            bestResults = new ArrayList<>();
        }
        if (max.isEqualTo(bestMaxValue)) {
            bestResults.add(resultContent);
        }
    }

    @Override
    public List<BacktestResultContent> getResult() {
        return bestResults;
    }
}
