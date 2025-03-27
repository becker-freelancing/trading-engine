package com.becker.freelance.capital.marketdata;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

public record MarketData(Pair pair, LocalDateTime closeTime, Decimal openBid, Decimal openAsk, Decimal highBid,
                         Decimal highAsk, Decimal lowBid, Decimal lowAsk, Decimal closeBid, Decimal closeAsk) {
}
