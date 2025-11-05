package com.becker.freelance.backtest.resultviewer.app;

import com.becker.freelance.backtest.commons.BacktestResultContent;
import com.becker.freelance.backtest.resultviewer.app.metric.MetricCalculator;
import com.becker.freelance.math.Decimal;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class BacktestResultPlotter implements ResultVisualizer {

    @Override
    public void visualize(String strategyName, BacktestResultContent baseData, List<BacktestResultContent> bestCumulative, List<BacktestResultContent> bestMax, List<BacktestResultContent> bestMin, List<BacktestResultContent> mostTrades, List<MetricCalculator> metrics) {
        XYChart cumulativeChart = plotResults(bestCumulative, "Bestes Kumulatives Ergebnis");
        XYChart maxChart = plotResults(bestMax, "Bestes Maximales Ergebnis");
        XYChart minChart = plotResults(bestMin, "Bestes Minimales Ergebnis");
        XYChart mostChart = plotResults(mostTrades, "Meiste Trades");

        ChartFrame chartFrame = new ChartFrame(cumulativeChart, maxChart, minChart, mostChart, bestCumulative.size() > 0 ? bestCumulative.get(0) : null);
        chartFrame.setTitle(strategyName);
        chartFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        chartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chartFrame.setVisible(true);
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
        chart.getStyler().setLegendVisible(false);

        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).isEmpty()) {
                chart.addSeries(legends.get(i), data.get(i));
            }
        }

        return chart;
    }

    private static class ChartFrame extends JFrame {

        private final XYChart cumulativeChart;
        private final XYChart maxChart;
        private final XYChart minChart;
        private final XYChart mostChart;
        private final BacktestResultContent firstBestCumulativeResult;

        public ChartFrame(XYChart cumulativeChart, XYChart maxChart, XYChart minChart, XYChart mostChart, BacktestResultContent firstBestCumulativeResult) throws HeadlessException {
            this.cumulativeChart = cumulativeChart;
            this.maxChart = maxChart;
            this.minChart = minChart;
            this.mostChart = mostChart;
            this.firstBestCumulativeResult = firstBestCumulativeResult;
            build();
        }

        private void build() {
            JPanel chartsPanel = new JPanel();
            chartsPanel.setLayout(new GridLayout(2, 2));

            chartsPanel.add(new XChartPanel<>(cumulativeChart));
            chartsPanel.add(new XChartPanel<>(maxChart));
            chartsPanel.add(new XChartPanel<>(minChart));
            chartsPanel.add(new XChartPanel<>(mostChart));

            JPanel parametersPanel = new JPanel();
            parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
            parametersPanel.add(new Label("Parameter der Besten Kumulativen Strategie"));
            parametersPanel.add(new Label("==================================================================="));
            parametersPanel.add(new Label());
            buildParametersText().forEach(parametersPanel::add);

            JPanel wrapperPanel = new JPanel();
            wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
            wrapperPanel.add(chartsPanel);
            wrapperPanel.add(parametersPanel);

            setContentPane(wrapperPanel);
        }

        private List<? extends Component> buildParametersText() {
            if (firstBestCumulativeResult == null) {
                return List.of(new Label("EXISTIERT NICHT"));
            }
            return Arrays.stream(firstBestCumulativeResult.parametersJson().split("\n"))
                    .map(Label::new)
                    .toList();
        }

    }
}
