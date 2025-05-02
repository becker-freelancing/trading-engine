package com.becker.freelance.capital.marketdata;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;

record BidMarketData(Pair pair, LocalDateTime closeTime, Decimal openBid, Decimal highBid, Decimal lowBid,
                     Decimal closeBid) {
}
