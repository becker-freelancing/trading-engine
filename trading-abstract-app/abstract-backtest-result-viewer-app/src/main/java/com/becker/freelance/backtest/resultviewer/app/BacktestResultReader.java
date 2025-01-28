package com.becker.freelance.backtest.resultviewer.app;

import org.apache.commons.compress.compressors.zstandard.ZstdCompressorInputStream;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class BacktestResultReader {

    private final Path resultPath;

    BacktestResultReader(Path resultPath) {
        this.resultPath = resultPath;
    }

    public Set<BacktestResultContent> readCsvContent() {
        Stream<String> lines = readLines(resultPath);
        return lines.filter(line -> !line.startsWith("pair")).map(this::toBacktestResultContent).collect(Collectors.toSet());
    }

    private BacktestResultContent toBacktestResultContent(String line) {
        String[] split = line.split(",");
        return new BacktestResultContent(
                split[0], LocalDateTime.parse(split[1]), LocalDateTime.parse(split[2]),
                Double.parseDouble(split[3]), Double.parseDouble(split[4]), Double.parseDouble(split[5]), Double.parseDouble(split[6]),
                parseParameters(split), parseTrades(split)
        );
    }

    public String parseParameters(String[] values) {
        List<String> params = new ArrayList<>();
        for (int i = 7; i < values.length; i++) {
            if (values[i].startsWith("\"[{")) {
                break;
            }
            params.add(values[i]);
        }
        return String.join(", ", params).replaceAll("\"\"", "\"");
    }

    public String parseTrades(String[] values) {
        List<String> params = new ArrayList<>();
        boolean add = false;
        for (int i = 7; i < values.length; i++) {
            if (values[i].equals("\"[]\"")){
                return "\"[]\"";
            }
            if (values[i].startsWith("\"[{")) {
                add = true;
            }
            if (values[i].contains("}]\"")){
                params.add(values[i]);
                break;
            }
            if (add) {
                params.add(values[i]);
            }
        }
        String tradesJson = String.join(", ", params).replaceAll("\"\"", "\"");
        return tradesJson.substring(1, tradesJson.length() - 1);
    }

    private static Stream<String> readLines(Path resultPath) {
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
