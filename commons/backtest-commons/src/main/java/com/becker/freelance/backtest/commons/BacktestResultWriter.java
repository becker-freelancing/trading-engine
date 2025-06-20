package com.becker.freelance.backtest.commons;

import com.becker.freelance.backtest.configuration.BacktestExecutionConfiguration;
import com.becker.freelance.backtest.util.PathUtil;
import com.becker.freelance.commons.app.AppConfiguration;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.commons.trade.Trade;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class BacktestResultWriter {

    private static final String HEADER = "pairs;app_mode;from_time;to_time;min;max;cumulative;initial_wallet_amount;parameter;trades\n";
    private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_kk-mm-ss");

    private final Path writePath;
    private final String baseString;

    private final ObjectMapper objectMapper;

    public BacktestResultWriter(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, String strategyName) {
        this(appConfiguration,
                backtestExecutionConfiguration,
                Path.of(formatFilePath(appConfiguration.applicationStartTime(), backtestExecutionConfiguration.pairs(), strategyName))
        );

    }

    public BacktestResultWriter(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, Path writePath) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule();
        module.addSerializer(Pair.class, new PairSerializer());
        module.addSerializer(TradeableQuantilMarketRegime.class, new TradeableMarketRegimeSerializer());
        objectMapper.registerModule(module);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.writePath = writePath;
        prepareCsvFile();
        this.baseString = formatBaseString(appConfiguration, backtestExecutionConfiguration, objectMapper);

        BacktestResultZipper.registerOnShutdown(writePath);
    }

    private static String formatBaseString(AppConfiguration appConfiguration, BacktestExecutionConfiguration backtestExecutionConfiguration, ObjectMapper objectMapper) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        try {
            return String.format("%s;%s;%s;%s;",
                    objectMapper.writeValueAsString(backtestExecutionConfiguration.pairs()), appConfiguration.appMode().getDescription(),
                    timeFormatter.format(backtestExecutionConfiguration.startTime()),
                    timeFormatter.format(backtestExecutionConfiguration.endTime())) + "%s;%s;%s;" + backtestExecutionConfiguration.initialWalletAmount() + ";%s;%s\n";
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not process " + backtestExecutionConfiguration.pairs() + " to JSON string", e);
        }
    }

    private static String formatFilePath(LocalDateTime startTime, List<Pair> pairs, String strategyName) {
        String pairName;
        if (pairs.size() == 1) {
            pairName = pairs.get(0).technicalName().replaceAll("/", "_").replaceAll(" ", "_");
        } else {
            pairName = "MULTIPLE_PAIR";
        }
        return PathUtil.fromRelativePath("results/" + strategyName + "/" + pairName + "__" + strategyName + "__" + BacktestResultWriter.FILE_NAME_FORMATTER.format(startTime) + ".csv");
    }

    private void prepareCsvFile() {
        if (!Files.exists(writePath)) {
            try {
                Files.createDirectories(writePath.getParent());
                Files.createFile(writePath);
                writeHeader();
            } catch (IOException e) {
                throw new IllegalStateException("Could not create Result File " + writePath);
            }
        }
    }

    private void writeHeader() throws IOException {
        Files.writeString(writePath, HEADER);
    }

    public synchronized void writeResult(List<Trade> trades, Map<String, Decimal> parameter) throws IOException {
        String line = buildLine(trades, parameter);
        Files.writeString(writePath, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    private String buildLine(List<Trade> trades, Map<String, Decimal> parameter) {
        List<Decimal> profits = trades.stream().map(Trade::getProfitInEuroWithFees).toList();
        Decimal sum = Decimal.ZERO;
        Decimal min = new Decimal(Double.MAX_VALUE);
        Decimal max = min.multiply(new BigDecimal("-1"));
        if (profits.isEmpty()) {
            min = Decimal.ZERO;
            max = Decimal.ZERO;
        }
        for (BigDecimal profit : profits) {
            sum = sum.add(profit);
            if (sum.isGreaterThan(max)) max = sum;
            if (sum.isLessThan(min)) min = sum;
        }

        String line = String.format(baseString,
                min, max, sum,
                map(parameter), map(trades));
        return line;
    }

    private String map(List<Trade> trades) {
        try {
            return objectMapper.writeValueAsString(trades);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String map(Map<String, Decimal> parameter) {
        try {
            return objectMapper.writeValueAsString(parameter);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
