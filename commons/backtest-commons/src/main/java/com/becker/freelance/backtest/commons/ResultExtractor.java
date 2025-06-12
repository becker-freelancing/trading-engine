package com.becker.freelance.backtest.commons;

import java.util.List;

public interface ResultExtractor {

    public void consume(BacktestResultContent resultContent);

    public List<BacktestResultContent> getResult();
}
