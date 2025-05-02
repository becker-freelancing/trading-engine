package com.becker.freelance.capital.marketdata;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

record AskMarketData(Pair pair, LocalDateTime closeTime, Decimal openAsk, Decimal highAsk, Decimal lowAsk,
                     Decimal closeAsk) {
}
