package com.becker.freelance.backtest.resultviewer.app.metric;

import java.util.List;

public class MultiMetric implements Writable {

    private final List<Writable> writables;

    public MultiMetric(List<Writable> writables) {
        this.writables = writables;
    }

    @Override
    public List<String> getLines() {
        return writables.stream().flatMap(writable -> writable.getLines().stream()).toList();
    }
}
