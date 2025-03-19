package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.math.Decimal;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class BinanceDataProvider extends DataProvider {


    private static final Logger logger = LoggerFactory.getLogger(BinanceDataProvider.class);

    @Override
    protected boolean supports(AppMode appMode) {
        return "BINANCE".equalsIgnoreCase(appMode.getDataSourceName()) && appMode.isDemo();
    }

    protected Map<LocalDateTime, TimeSeriesEntry> mapWithSpread(List<String[]> rows, Pair pair, LocalDateTime from, LocalDateTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beforeFrom = from.minusDays(3);
        return rows.stream().skip(1).parallel().map(row -> {
                    LocalDateTime time = LocalDateTime.parse(row[0], formatter);
                    if (time.isBefore(beforeFrom) || time.isAfter(to)) {
                        return null;
                    }
                    Decimal openBid = new Decimal(row[1]);
                    Decimal openAsk = new Decimal(row[2]);
                    Decimal highBid = new Decimal(row[3]);
                    Decimal highAsk = new Decimal(row[4]);
                    Decimal lowBid = new Decimal(row[5]);
                    Decimal lowAsk = new Decimal(row[6]);
                    Decimal closeBid = new Decimal(row[7]);
                    Decimal closeAsk = new Decimal(row[8]);
                    Decimal volume = new Decimal(row[9]);
                    Decimal trades = new Decimal(row[11]);
                    return new TimeSeriesEntry(
                            time, openBid, openAsk, highBid, highAsk, lowBid, lowAsk,
                            closeBid, closeAsk, volume, trades, pair
                    );
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(TimeSeriesEntry::time, entry -> entry, (existing, replacement) -> existing));

    }

    @Override
    public TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException {
        logger.info("Start reading TimeSeries {}...", pair.technicalName());
        String filePath = PathUtil.fromRelativePath("data-binance\\" + getFilename(pair) + ".zip");

        // Read CSV from ZIP file
        ZipFile zipFile = new ZipFile(filePath);
        CSVReader reader = new CSVReader(new InputStreamReader(zipFile.getInputStream(zipFile.entries().nextElement())));

        List<String[]> rows = null;
        try {
            rows = reader.readAll();
        } catch (CsvException e) {
            throw new IOException(e);
        }
        Map<LocalDateTime, TimeSeriesEntry> entries;

        if (pair.equals(Pair.eurUsd1())) {
            entries = mapEurUsd(rows, pair, from, to);
        } else {
            entries = mapWithSpread(rows, pair, from, to);
        }
        TimeSeries timeSeries = map(pair, from, to, entries);
        logger.info("Finished reading TimeSeries {}", pair.technicalName());
        return timeSeries;
    }

    private Map<LocalDateTime, TimeSeriesEntry> mapEurUsd(List<String[]> rows, Pair pair, LocalDateTime from, LocalDateTime to) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beforeFrom = from.minusDays(3);
        return rows.stream().skip(1).parallel().map(row -> {
                    LocalDateTime time = LocalDateTime.parse(row[0], formatter);
                    if (time.isBefore(beforeFrom) || time.isAfter(to)) {
                        return null;
                    }
                    Decimal openBid = new Decimal(row[1]);
                    Decimal openAsk = new Decimal(row[2]);
                    Decimal highBid = new Decimal(row[3]);
                    Decimal highAsk = new Decimal(row[4]);
                    Decimal lowBid = new Decimal(row[5]);
                    Decimal lowAsk = new Decimal(row[6]);
                    Decimal closeBid = new Decimal(row[7]);
                    Decimal closeAsk = new Decimal(row[8]);
                    Decimal volume = Decimal.ZERO;
                    Decimal trades = Decimal.ZERO;
                    return new TimeSeriesEntry(
                            time, openBid, openAsk, highBid, highAsk, lowBid, lowAsk,
                            closeBid, closeAsk, volume, trades, pair
                    );
                }).filter(Objects::nonNull)
                .collect(Collectors.toMap(TimeSeriesEntry::time, entry -> entry, (existing, replacement) -> existing));
    }

    private String getFilename(Pair pair) {
        return pair.baseCurrency() + pair.counterCurrency() + "_" + pair.timeInMinutes() + ".csv";
    }
}
