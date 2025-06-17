package com.becker.freelance.data.csv;

import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public class CsvDataReader {

    private final RowMappingInfo rowMappingInfo;
    private final InputStream fileInputStream;
    private final DateTimeFormatter timeFormatter;
    private final Pair pair;

    public CsvDataReader(RowMappingInfo rowMappingInfo, InputStream fileInputStream, DateTimeFormatter timeFormatter, Pair pair) {
        this.rowMappingInfo = rowMappingInfo;
        this.fileInputStream = fileInputStream;
        this.timeFormatter = timeFormatter;
        this.pair = pair;
    }

    public Stream<TimeSeriesEntry> readCsvParallel() {
        Stream<String[]> lines = readLines();
        return lines.parallel().skip(rowMappingInfo.containsHeader() ? 1 : 0)
                .filter(line -> line.length > 1)
                .map(this::mapLine);
    }

    private TimeSeriesEntry mapLine(String[] line) {
        LocalDateTime time = LocalDateTime.parse(line[0], timeFormatter);
        Decimal openBid = new Decimal(line[rowMappingInfo.getOpenBidIdx()]);
        Decimal openAsk = new Decimal(line[rowMappingInfo.getOpenAskIdx()]);
        Decimal highBid = new Decimal(line[rowMappingInfo.getHighBidIdx()]);
        Decimal highAsk = new Decimal(line[rowMappingInfo.getHighAskIdx()]);
        Decimal lowBid = new Decimal(line[rowMappingInfo.getLowBidIdx()]);
        Decimal lowAsk = new Decimal(line[rowMappingInfo.getLowAskIdx()]);
        Decimal closeBid = new Decimal(line[rowMappingInfo.getCloseBidIdx()]);
        Decimal closeAsk = new Decimal(line[rowMappingInfo.getCloseAskIdx()]);
        Decimal volume = rowMappingInfo.getVolumeIdx().map(idx -> new Decimal(line[idx])).orElse(Decimal.ZERO);
        Decimal trades = rowMappingInfo.getTradesIdx().map(idx -> new Decimal(line[idx])).orElse(Decimal.ZERO);

        return new TimeSeriesEntry(time, openBid, openAsk, highBid, highAsk, lowBid, lowAsk, closeBid, closeAsk, volume, trades, pair);
    }

    private Stream<String[]> readLines() {
        CSVReader reader = new CSVReader(new InputStreamReader(fileInputStream));
        try {
            return reader.readAll().stream();
        } catch (IOException | CsvException e) {
            throw new IllegalStateException("Could not read content of CSV-File", e);
        }
    }
}
