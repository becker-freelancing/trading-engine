package com.becker.freelance.backtest.resultviewer.app.callback;

import java.nio.file.Path;
import java.util.List;

public interface ParsedCallback {

    public static ParsedCallback noop() {
        return new ParsedCallback() {
            @Override
            public void onBestCumulative(List<ParsedBacktestResult> bestCumulative, Path resultPath) {

            }

            @Override
            public void onBestMax(List<ParsedBacktestResult> bestMax, Path resultPath) {

            }

            @Override
            public void onBestMin(List<ParsedBacktestResult> bestMin, Path resultPath) {

            }

            @Override
            public void onMostTrades(List<ParsedBacktestResult> mostTrades, Path resultPath) {

            }
        };
    }

    public void onBestCumulative(List<ParsedBacktestResult> bestCumulative, Path resultPath);

    public void onBestMax(List<ParsedBacktestResult> bestMax, Path resultPath);

    public void onBestMin(List<ParsedBacktestResult> bestMin, Path resultPath);

    public void onMostTrades(List<ParsedBacktestResult> mostTrades, Path resultPath);
}
