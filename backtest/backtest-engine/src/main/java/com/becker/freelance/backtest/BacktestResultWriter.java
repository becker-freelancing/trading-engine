package com.becker.freelance.backtest;

import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.ExecutionConfiguration;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.position.Trade;
import com.becker.freelance.strategies.BaseStrategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class BacktestResultWriter {

    private static final String HEADER = "pair,from_time,to_time,min,max,cumulative,initial_wallet_amount,parameter,trades\n";

    private final Path writePath;
    private final String baseString;

    private final ObjectMapper objectMapper;

    public BacktestResultWriter(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String strategyName = baseStrategy.getName();
        DateTimeFormatter fileDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
        this.writePath = Path.of(formatFilePath(appConfiguration.startTime(), executionConfiguration.pair(), strategyName, fileDateFormatter));
        prepareCsvFile();
        this.baseString = formatBaseString(executionConfiguration);

        BacktestResultZipper.registerOnShutdown(writePath);
    }

    private static String formatBaseString(ExecutionConfiguration executionConfiguration) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        return String.format("%s,%s,%s,",
                executionConfiguration.pair().technicalName(),
                timeFormatter.format(executionConfiguration.startTime()),
                timeFormatter.format(executionConfiguration.endTime())) + "%s,%s,%s," + executionConfiguration.initialWalletAmount() + ",%s,%s\n";
    }

    private void prepareCsvFile() {
        if (!Files.exists(writePath)){
            try {
                Files.createDirectories(writePath.getParent());
                Files.createFile(writePath);
                writeHeader();
            } catch (IOException e){
                throw new IllegalStateException("Could not create Result File " + writePath);
            }
        }
    }

    private String formatFilePath(LocalDateTime startTime, Pair pair, String strategyName, DateTimeFormatter fileDateFormatter) {
        String pairName = pair.technicalName().replaceAll("/", "_").replaceAll(" ", "_");
        return PathUtil.fromRelativePath(".results\\" + strategyName + "\\" + pairName + "__" + strategyName + "__" + fileDateFormatter.format(startTime) + ".csv");
    }

    private void writeHeader() throws IOException {
        Files.writeString(writePath, HEADER);
    }

    public synchronized void writeResult(List<Trade> trades, Map<String, Double> parameter) throws IOException {
        String line = buildLine(trades, parameter);
        Files.writeString(writePath, line, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
    }

    private String buildLine(List<Trade> trades, Map<String, Double> parameter) {
        List<Double> profits = trades.stream().map(Trade::getProfitInEuro).toList();
        double sum = 0.;
        double min = Double.MAX_VALUE;
        double max = -min;
        if (profits.isEmpty()){
            min = 0;
            max = 0;
        }
        for (Double profit : profits) {
            sum += profit;
            if (sum > max) max = sum;
            if (sum < min) min = sum;
        }

        String line = String.format(baseString,
                min, max, sum,
                map(parameter), map(trades));
        return line;
    }

    private String map(List<Trade> trades) {
        try {
            return "\"" + objectMapper.writeValueAsString(trades) + "\"";
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String map(Map<String, Double> parameter) {
        try {
            return "\"" + objectMapper.writeValueAsString(parameter) + "\"";
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
