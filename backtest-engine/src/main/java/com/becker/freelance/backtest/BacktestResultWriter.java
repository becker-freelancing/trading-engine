package com.becker.freelance.backtest;

import com.becker.freelance.commons.AppConfiguration;
import com.becker.freelance.commons.ExecutionConfiguration;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.commons.Trade;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class BacktestResultWriter {

    private final String HEADER = "pair,from_time,to_time,min,max,cumulative,initial_wallet_amount,parameter,trades";

    private final AppConfiguration appConfiguration;
    private final ExecutionConfiguration executionConfiguration;
    private final BaseStrategy baseStrategy;
    private final Path writePath;
    private final String baseString;

    private final ObjectMapper objectMapper;

    public BacktestResultWriter(AppConfiguration appConfiguration, ExecutionConfiguration executionConfiguration, BaseStrategy baseStrategy){
        this.appConfiguration = appConfiguration;
        this.executionConfiguration = executionConfiguration;
        this.baseStrategy = baseStrategy;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String strategyName = baseStrategy.getName();
        DateTimeFormatter fileDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm-ss");
        this.writePath = Path.of(PathUtil.fromRelativePath(".results\\" + strategyName + "\\" + strategyName + "__" + fileDateFormatter.format(appConfiguration.getStartTime()) + ".csv"));
        if (!Files.exists(writePath)){
            try {
                Files.createDirectories(writePath.getParent());
                Files.createFile(writePath);
                writeHeader();
            } catch (IOException e){
                throw new IllegalStateException("Could not create Result File " + writePath);
            }
        }
        DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_DATE_TIME;
        this.baseString = String.format("%s,%s,%s,",
                executionConfiguration.getPair(),
                timeFormatter.format(executionConfiguration.getStartTime()),
                timeFormatter.format(executionConfiguration.getEndTime())) + "%s,%s,%s," + executionConfiguration.getInitialWalletAmount() + ",%s,%s\n";

        BacktestResultZipper.registerOnShutdown(writePath);
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
