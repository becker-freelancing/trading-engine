package com.becker.freelance.backtest.commons;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.math.Decimal;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BacktestResultReader {

    private static final Logger logger = LoggerFactory.getLogger(BacktestResultReader.class);

    private final Path resultPath;
    private final ObjectMapper objectMapper;

    public BacktestResultReader(Path resultPath) {
        this.resultPath = resultPath;
        this.objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Pair.class, new PairDeserializer());
        objectMapper.registerModule(module);
    }

    public Set<BacktestResultContent> readCsvContent() {
        return streamCsvContent().collect(Collectors.toSet());
    }

    public Stream<BacktestResultContent> streamCsvContent() {
        logger.info("Reading Backtest Results from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.parallel().filter(line -> !line.startsWith("pair")).map(this::toBacktestResultContent);
    }

    public Stream<BacktestResultContent> streamCsvContentWithMinValue(Decimal minValue) {
        logger.info("Reading Backtest Results from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.parallel().filter(line -> !line.startsWith("pair")).filter(line -> {
            String[] split = line.split(",");
            Decimal currMin = new Decimal(split[4]);
            return minValue.isEqualTo(currMin);
        }).map(this::toBacktestResultContent);
    }

    public Stream<Decimal> streamMinValues(){
        logger.info("Reading Min Result Values from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.filter(line -> !line.startsWith("pair")).map(line -> line.split(",")).map(split -> new Decimal(split[4]));
    }

    public Stream<BacktestResultContent> streamCsvContentWithMaxValue(Decimal maxValue) {
        logger.info("Reading Backtest Results from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.parallel().filter(line -> !line.startsWith("pair")).filter(line -> {
            String[] split = line.split(",");
            Decimal currMin = new Decimal(split[5]);
            return maxValue.isEqualTo(currMin);
        }).map(this::toBacktestResultContent);
    }

    public Stream<Decimal> streamMaxValues(){
        logger.info("Reading Max Result Values from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.filter(line -> !line.startsWith("pair")).map(line -> line.split(",")).map(split -> new Decimal(split[5]));
    }

    public Stream<BacktestResultContent> streamCsvContentWithCumulativeValue(Decimal cumulativeValue) {
        logger.info("Reading Backtest Results from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.parallel().filter(line -> !line.startsWith("pair")).filter(line -> {
            String[] split = line.split(",");
            Decimal currMin = new Decimal(split[6]);
            return cumulativeValue.isEqualTo(currMin);
        }).map(this::toBacktestResultContent);
    }

    public Stream<Decimal> streamCumulativeValues(){
        logger.info("Reading Cumulative Result Values from {}", resultPath);
        Stream<String> lines = readLines(resultPath);
        return lines.filter(line -> !line.startsWith("pair")).map(line -> line.split(",")).map(split -> new Decimal(split[6]));
    }

    private BacktestResultContent toBacktestResultContent(String line) {
        String[] split = line.split(",");
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

    private Stream<String> readLines(Path resultPath) {
        try (
                FileInputStream fis = new FileInputStream(resultPath.toFile());
                ZstdCompressorInputStream zis = new ZstdCompressorInputStream(fis);
                BufferedReader reader = new BufferedReader(new InputStreamReader(zis))
        ){

            Stream.Builder<String> builder = Stream.builder();
            String line;
            while ((line = reader.readLine()) != null){
                builder.add(line);
            }
            return builder.build();
        } catch (IOException e) {
            throw new IllegalStateException("Could not read file " + resultPath, e);
        }
    }
}
