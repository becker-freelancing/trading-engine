package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.regime.TradeableQuantilMarketRegime;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BacktestResultReader {

    private static final Logger logger = LoggerFactory.getLogger(BacktestResultReader.class);

    private final Path resultPath;
    private final ObjectMapper objectMapper;

    public BacktestResultReader(Path resultPath) {
        this.resultPath = resultPath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        SimpleModule module = new SimpleModule(Pair.class.getName());
        module.addDeserializer(Pair.class, new PairDeserializer());
        module.addDeserializer(TradeableQuantilMarketRegime.class, new TradeableMarketRegimeDeserializer());
        objectMapper.registerModule(module);
    }

    public Set<BacktestResultContent> readCsvContent() {
        if (true) {
            throw new UnsupportedOperationException("Method streamCsvContent does not return all Contents");
        }
        return streamCsvContent().collect(Collectors.toSet());
    }

    public Stream<BacktestResultContent> streamCsvContent() {
        logger.info("Reading Backtest Results from {}", resultPath);
        BacktestResultContent[] result = new BacktestResultContent[]{null};
        Consumer<String> lineConsumer = line -> {
            if (result[0] != null) {
                return;
            }
            if (line.startsWith("pairs")) {
                return;
            }
            result[0] = toBacktestResultContent(line);
        };
        readLines(resultPath, lineConsumer);
        return Stream.of(result[0]);
    }

    public Stream<BacktestResultContent> streamCsvContentWithMinValue() {
        logger.info("Reading Backtest Results from {}", resultPath);
        List<BacktestResultContent> resultContents = new ArrayList<>();
        Decimal[] currentBestMin = new Decimal[]{Decimal.DOUBLE_MAX.multiply(-1)};
        Consumer<String> consumer = line -> {
            if (line.startsWith("pairs")) {
                return;
            }
            String[] split = line.split(";");
            Decimal currMin = new Decimal(split[4]);
            if (currMin.isLessThan(currentBestMin[0])) {
                return;
            }
            if (currMin.isGreaterThan(currentBestMin[0])) {
                currentBestMin[0] = currMin;
                resultContents.clear();
            }
            resultContents.add(toBacktestResultContent(line));
        };
        readLines(resultPath, consumer);
        return resultContents.stream();
    }

    public Stream<BacktestResultContent> streamCsvContentWithMaxValue() {
        logger.info("Reading Backtest Results from {}", resultPath);
        List<BacktestResultContent> resultContents = new ArrayList<>();
        Decimal[] bestMaxValue = new Decimal[]{Decimal.DOUBLE_MAX.multiply(-1)};
        Consumer<String> consumer = line -> {
            if (line.startsWith("pairs")) {
                return;
            }
            String[] split = line.split(";");
            Decimal currMax = new Decimal(split[5]);
            if (currMax.isLessThan(bestMaxValue[0])) {
                return;
            }
            if (currMax.isGreaterThan(bestMaxValue[0])) {
                bestMaxValue[0] = currMax;
                resultContents.clear();
            }
            resultContents.add(toBacktestResultContent(line));
        };
        readLines(resultPath, consumer);
        return resultContents.stream();
    }


    public Stream<BacktestResultContent> streamCsvContentWithCumulativeValue() {
        logger.info("Reading Backtest Results from {}", resultPath);
        List<BacktestResultContent> resultContents = new ArrayList<>();
        Decimal[] bestCum = new Decimal[]{Decimal.DOUBLE_MAX.multiply(-1)};
        Consumer<String> consumer = line -> {
            if (line.startsWith("pairs")) {
                return;
            }
            String[] split = line.split(";");
            Decimal currMin = new Decimal(split[6]);
            if (currMin.isLessThan(bestCum[0])) {
                return;
            }
            if (currMin.isGreaterThan(bestCum[0])) {
                bestCum[0] = currMin;
                resultContents.clear();
            }
            resultContents.add(toBacktestResultContent(line));
        };
        readLines(resultPath, consumer);
        return resultContents.stream();
    }


    private BacktestResultContent toBacktestResultContent(String line) {
        String[] split = line.split(";");
        return new BacktestResultContent(objectMapper,
                split[0], split[1], LocalDateTime.parse(split[2]), LocalDateTime.parse(split[3]),
                new Decimal(split[4]), new Decimal(split[5]), new Decimal(split[6]), new Decimal(split[7]),
                parseParameters(split), parseTrades(split)
        );
    }

    public String parseParameters(String[] values) {
        List<String> params = new ArrayList<>();
        for (int i = 8; i < values.length; i++) {
            if (values[i].startsWith("[{")) {
                break;
            }
            params.add(values[i]);
        }
        String parametersJson = String.join(", ", params).replaceAll("\"\"", "\"");
        return parametersJson;
    }

    public String parseTrades(String[] values) {
        List<String> params = new ArrayList<>();
        boolean add = false;
        for (int i = 7; i < values.length; i++) {
            if (values[i].equals("[]")){
                return "[]";
            }
            if (values[i].startsWith("[{")) {
                add = true;
            }
            if (values[i].contains("}]")){
                params.add(values[i]);
                break;
            }
            if (add) {
                params.add(values[i]);
            }
        }
        String tradesJson = String.join(", ", params).replaceAll("\"\"", "\"");
        return tradesJson;
    }

    private void readLines(Path resultPath, Consumer<String> lineConsumer) {
        try (
                FileInputStream fis = new FileInputStream(resultPath.toFile());
                ZstdCompressorInputStream zis = new ZstdCompressorInputStream(fis);
                BufferedReader reader = new BufferedReader(new InputStreamReader(zis))
        ){

            String line;
            while ((line = reader.readLine()) != null){
                lineConsumer.accept(line);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file " + resultPath, e);
        }
    }

    public void readCsvContent(Path resultPath, ResultExtractor... resultExtractors) {
        Consumer<String> mapper = line -> {
            if (line.startsWith("pair")) {
                return;
            }
            BacktestResultContent backtestResultContent = toBacktestResultContent(line);
            for (ResultExtractor resultExtractor : resultExtractors) {
                resultExtractor.consume(backtestResultContent);
            }
        };

        readLines(resultPath, mapper);
    }
}
