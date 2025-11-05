package com.becker.freelance.backtest.resultviewer.app.extractor;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.commons.ResultExtractor;
import com.becker.freelance.math.Decimal;

import java.util.ArrayList;
import java.util.List;

public class BestCumulativeExtractor implements ResultExtractor {

    private Decimal bestCumulativeValue = Decimal.MINUS_DOUBLE_MAX;
    private List<BacktestResultContent> bestResults = new ArrayList<>();

    @Override
    public void consume(BacktestResultContent resultContent) {
        Decimal cumulative = resultContent.cumulative();
        if (cumulative.isGreaterThan(bestCumulativeValue)) {
            bestCumulativeValue = cumulative;
            bestResults = new ArrayList<>();
        }

        if (cumulative.isEqualTo(bestCumulativeValue)) {
            bestResults.add(resultContent);
        }
    }

    @Override
    public List<BacktestResultContent> getResult() {
        return bestResults;
    }
}
