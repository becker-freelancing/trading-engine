package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.math.Decimal;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.util.ArrayList;
import java.util.List;

class BacktestResultPlotter implements Runnable {

    private final List<BacktestResultContent> bestCumulative;
    private final List<BacktestResultContent> bestMax;
    private final List<BacktestResultContent> bestMin;

    public BacktestResultPlotter(List<BacktestResultContent> bestCumulative, List<BacktestResultContent> bestMax, List<BacktestResultContent> bestMin) {
        this.bestCumulative = bestCumulative;
        this.bestMax = bestMax;
        this.bestMin = bestMin;
    }

    @Override
    public void run() {
        XYChart cumulativeChart = plotResults(bestCumulative, "Bestes Kumulatives Ergebnis");
        XYChart maxChart = plotResults(bestMax, "Bestes Maximales Ergebnis");
        XYChart minChart = plotResults(bestMin, "Bestes Minimales Ergebnis");

        new SwingWrapper<>(List.of(cumulativeChart, maxChart, minChart)).displayChartMatrix();
    }

    private XYChart plotResults(List<BacktestResultContent> backtestResultContents, String title) {
        List<String> legends = new ArrayList<>();
        List<List<Decimal>> data = new ArrayList<>();

        backtestResultContents.forEach(resultContent -> {
            List<Decimal> tradeProfits = resultContent.tradeProfits();
            List<Decimal> series = new ArrayList<>();
            Decimal sum = Decimal.ZERO;
            for (Decimal tradeProfit : tradeProfits) {
                sum = sum.add(tradeProfit);
                series.add(sum);
            }
            data.add(series);
            legends.add(resultContent.parametersJson());
        });

        XYChart chart = new XYChartBuilder().title(title).xAxisTitle("Trade Index").yAxisTitle("Cumulative Profit").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);

        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isEmpty()) {
                chart.addSeries(legends.get(i), data.get(i));
            }
        }

        return chart;
    }
}
