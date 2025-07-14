package com.becker.freelance.bybit.marketdata;

import com.becker.freelance.bybit.util.PairConverter;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.MarketInterval;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.restApi.BybitApiMarketRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

public class MarketDataClient {

    private static final Logger logger = LoggerFactory.getLogger(MarketDataClient.class);

    private final BybitApiMarketRestClient marketRestClient;
    private final PairConverter pairConverter;

    public MarketDataClient() {
        this.marketRestClient = BybitApiClientFactory.newInstance().newMarketDataRestClient();
        this.pairConverter = new PairConverter();
    }

    private static LocalDateTime map(String timeMs) {
        return Instant.ofEpochMilli(Long.parseLong(timeMs))
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }


    public List<TimeSeriesEntry> getPriceInRange(Pair pair, LocalDateTime from, LocalDateTime to) {
        MarketDataRequest request = MarketDataRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(pair))
                .start(map(from))
                .end(map(to))
                .marketInterval(MarketInterval.ONE_MINUTE)
                .build();
        Map<String, Object> marketLinesData = (Map<String, Object>) marketRestClient.getMarketLinesData(request);

        if (!"OK".equals(marketLinesData.get("retMsg"))) {
            logger.error("Could not request market data for pair {} from {} to {}, because of {}", pair.technicalName(), from, to, marketLinesData.get("retMsg"));
            throw new IllegalStateException("Could not request market data");
        }

        Map<String, Object> result = (Map<String, Object>) marketLinesData.get("result");
        List<List<String>> list = (List<List<String>>) result.get("list");

        return list.stream()
                .map(data -> map(data, pair))
                .filter(entry -> entry.time().isBefore(to) || entry.time().equals(to))
                .filter(entry -> entry.time().isAfter(from) || entry.time().equals(from))
                .toList();

    }

    public TimeSeriesEntry getPriceForTime(Pair pair, LocalDateTime time) {
        MarketDataRequest request = MarketDataRequest.builder()
                .category(CategoryType.LINEAR)
                .symbol(pairConverter.convert(pair))
                .end(map(time))
                .limit(3)
                .marketInterval(MarketInterval.ONE_MINUTE)
                .build();
        Map<String, Object> marketLinesData = (Map<String, Object>) marketRestClient.getMarketLinesData(request);

        if (!"OK".equals(marketLinesData.get("retMsg"))) {
            logger.error("Could not request market data for pair {} at time {}, because of {}", pair.technicalName(), time, marketLinesData.get("retMsg"));
            throw new IllegalStateException("Could not request market data");
        }

        Map<String, Object> result = (Map<String, Object>) marketLinesData.get("result");
        List<List<String>> list = (List<List<String>>) result.get("list");

        return list.stream()
                .map(data -> map(data, pair))
                .filter(entry -> entry.time().equals(time))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("Could not request market data"));
    }

    private TimeSeriesEntry map(List<String> data, Pair pair) {
        LocalDateTime start = map(data.get(0));
        LocalDateTime end = start.plusMinutes(1);
        return new TimeSeriesEntry(
                end,
                new Decimal(data.get(1)), new Decimal(data.get(1)),
                new Decimal(data.get(2)), new Decimal(data.get(2)),
                new Decimal(data.get(3)), new Decimal(data.get(3)),
                new Decimal(data.get(4)), new Decimal(data.get(4)),
                new Decimal(data.get(5)), Decimal.ZERO,
                pair
        );
    }

    private Long map(LocalDateTime time) {
        return time
                .toInstant(ZoneId.systemDefault().getRules().getOffset(time))
                .toEpochMilli();
    }

}
