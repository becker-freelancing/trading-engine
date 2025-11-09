package com.becker.freelance.trading.external.services.metric;

import com.becker.freelance.trading.external.services.registry.NoParamsExternalServiceBuilder;

public interface MetricCalculatorBuilder<METRIC extends MetricCalculator> extends NoParamsExternalServiceBuilder<METRIC> {

    @Override
    METRIC build();
}
