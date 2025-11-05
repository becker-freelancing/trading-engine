package com.becker.freelance.backtest.resultviewer.app.metric;

import java.util.List;

public record Metric(String name, Object value, String unit) implements Writable {
    @Override
    public List<String> getLines() {
        return List.of(String.format("%s: %s %s", name(), value(), unit()));
    }
}
