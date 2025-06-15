package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BacktestResultContentTest {

    BacktestResultContent resultContent;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule(Pair.class.getName());
        module.addDeserializer(Pair.class, new PairDeserializer());
        module.addDeserializer(TradeableQuantilMarketRegime.class, new TradeableMarketRegimeDeserializer());
        objectMapper.registerModule(module);

        resultContent = new BacktestResultContent(
                objectMapper, "EUR/USD M1", "TEST_DEMO", LocalDateTime.MIN, LocalDateTime.MAX, Decimal.ZERO, Decimal.TEN, Decimal.TWO, Decimal.DOUBLE_MAX,
                """
                        {"size": 0.2, "tp": 20}
                        """,
                """
                        [{"openTime":"2023-01-01T05:20:00","closeTime":"2023-01-01T05:25:00","pair":{"technicalName":"GLD/USD M5"},"profitInEuroWithFees":10.0,"openLevel":1811.83,"closeLevel":1811.73,"size":0.2,"direction":"SELL","conversionRate":1.0716,"positionType":"HARD_LIMIT"},
                        {"openTime":"2023-01-01T05:20:00","closeTime":"2023-01-01T05:25:00","pair":{"technicalName":"GLD/USD M5"},"profitInEuroWithFees":20.0,"openLevel":1811.83,"closeLevel":1811.73,"size":0.2,"direction":"SELL","conversionRate":1.0716,"positionType":"HARD_LIMIT"}]
                        """
        );
    }

    @Test
    void tradeProfits() {
        List<Decimal> profits = resultContent.tradeProfits();
        assertEquals(List.of(new Decimal("10.0"), new Decimal("20.0")), profits);
    }

    @Test
    void parameters() {
        Map<String, Decimal> parameters = resultContent.parameters();
        Map<String, Decimal> expected = Map.of(
                "size", new Decimal("0.2"),
                "tp", new Decimal("20")
        );

        assertEquals(expected, parameters);
    }

}