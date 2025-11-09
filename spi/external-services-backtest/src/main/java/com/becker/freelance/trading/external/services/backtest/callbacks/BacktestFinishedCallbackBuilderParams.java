package com.becker.freelance.trading.external.services.backtest.callbacks;

import com.becker.freelance.commons.pair.Pair;

import java.time.LocalDateTime;
import java.util.List;

public record BacktestFinishedCallbackBuilderParams(List<Pair> pairs,
                                                    String strategyName,
                                                    LocalDateTime startTime) {
}
