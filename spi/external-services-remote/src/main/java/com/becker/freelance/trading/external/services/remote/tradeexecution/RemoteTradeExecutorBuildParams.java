package com.becker.freelance.trading.external.services.remote.tradeexecution;

import com.becker.freelance.commons.calculation.EurUsdRequestor;
import com.becker.freelance.commons.pair.Pair;

public record RemoteTradeExecutorBuildParams(
        Pair pair,
        EurUsdRequestor eurUsdRequestor
) {
}
