package com.becker.freelance.trading.external.services.metric;

import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.trading.external.services.registry.ExternalService;

import java.util.List;

public interface MetricCalculator<METRIC extends Metric<METRICVALUE>, METRICVALUE> extends ExternalService {

    public METRIC calculate(List<Trade> trades);
}
