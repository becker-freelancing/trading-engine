package com.becker.freelance.data;

import com.becker.freelance.backtest.util.PathUtil;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;
import com.becker.freelance.data.csv.CsvDataReader;
import com.becker.freelance.data.csv.RowMappingInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class BinanceDataProvider extends DataProvider {

    private static final Logger logger = LoggerFactory.getLogger(BinanceDataProvider.class);
    private static final RowMappingInfo WITH_SPREAD_MAPPING_INFO = new RowMappingInfo(true, 0, 1, 2, 4, 4, 5, 6, 7, 8, 9, 11);
    private static final RowMappingInfo EUR_USD_MAPPING_INFO = new RowMappingInfo(true, 0, 1, 2, 4, 4, 5, 6, 7, 8);

    private final Pair pair;

    public BinanceDataProvider(Pair pair) {
        this.pair = pair;
    }

    @Override
    public TimeSeries readTimeSeries(LocalDateTime from, LocalDateTime to) {
        logger.info("Start reading TimeSeries {}...", pair.technicalName());
        String filePath = PathUtil.fromRelativePath("data-binance/" + getFilename() + ".zip");


        Map<LocalDateTime, TimeSeriesEntry> entries;

        // Read CSV from ZIP file
        try (ZipFile zipFile = new ZipFile(filePath)) {
            InputStream inputStream = zipFile.getInputStream(zipFile.entries().nextElement());

            if (pair.equals(Pair.eurUsd1())) {
                entries = readCsvContent(inputStream, from, to, EUR_USD_MAPPING_INFO);
            } else {
                entries = readCsvContent(inputStream, from, to, WITH_SPREAD_MAPPING_INFO);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read TimeSeries for " + pair, e);
        }

        TimeSeries timeSeries = map(pair, from, to, entries);
        logger.info("Finished reading TimeSeries {}", pair.technicalName());
        return timeSeries;
    }

    private Map<LocalDateTime, TimeSeriesEntry> readCsvContent(InputStream fileInputStream, LocalDateTime from, LocalDateTime to, RowMappingInfo mappingInfo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime beforeFrom = from.minusDays(3);
        return new CsvDataReader(mappingInfo, fileInputStream, formatter, pair).readCsvParallel()
                .filter(entry -> entry.time().isAfter(beforeFrom))
                .filter(entry -> entry.time().isBefore(to))
                .collect(Collectors.toMap(TimeSeriesEntry::time, entry -> entry, (existing, replacement) -> existing));
    }

    private String getFilename() {
        return pair.baseCurrency() + pair.counterCurrency() + "_" + pair.timeInMinutes() + ".csv";
    }
}
