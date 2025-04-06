package com.becker.freelance.broker.orderbook;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;

import java.time.LocalDateTime;
import java.util.List;

public record Orderbook(Pair pair, LocalDateTime time, String type, List<Decimal> bidValue, List<Decimal> bidQuantity,
                        List<Decimal> askValue, List<Decimal> askQuantity) {
}
