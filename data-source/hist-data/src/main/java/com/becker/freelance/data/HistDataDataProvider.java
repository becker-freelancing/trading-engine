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
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class HistDataDataProvider extends DataProvider {

    private static final Logger logger = LoggerFactory.getLogger(HistDataDataProvider.class);

    @Override
    protected boolean supports(AppMode appMode) {
        return "HISTDATA".equalsIgnoreCase(appMode.getDataSourceName()) && appMode.isDemo();
    }

    protected Map<LocalDateTime, TimeSeriesEntry> mapWithSpread(List<String[]> rows, Pair pair) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return rows.stream().skip(1).parallel().map(row -> {
                    LocalDateTime time = LocalDateTime.parse(row[0], formatter);
                    Decimal openBid = new Decimal(row[1]);
                    Decimal openAsk = new Decimal(row[2]);
                    Decimal highBid = new Decimal(row[3]);
                    Decimal highAsk = new Decimal(row[4]);
                    Decimal lowBid = new Decimal(row[5]);
                    Decimal lowAsk = new Decimal(row[6]);
                    Decimal closeBid = new Decimal(row[7]);
                    Decimal closeAsk = new Decimal(row[8]);
                    return new TimeSeriesEntry(
                            time, openBid, openAsk, highBid, highAsk, lowBid, lowAsk,
                            closeBid, closeAsk, Decimal.ZERO, Decimal.ZERO, pair
                    );
                })
                .collect(Collectors.toMap(TimeSeriesEntry::time, entry -> entry, (existing, replacement) -> existing));

    }

    @Override
    public TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException {
        logger.info("Start reading TimeSeries {}...", pair.technicalName());
        String filePath = PathUtil.fromRelativePath("data-histdata\\" + getFilename(pair) + ".zip");

        // Read CSV from ZIP file
        ZipFile zipFile = new ZipFile(filePath);
        CSVReader reader = new CSVReader(new InputStreamReader(zipFile.getInputStream(zipFile.entries().nextElement())));

        List<String[]> rows = null;
        try {
            rows = reader.readAll();
        } catch (CsvException e) {
            throw new IOException(e);
        }
        Map<LocalDateTime, TimeSeriesEntry> entries = mapWithSpread(rows, pair);
        TimeSeries timeSeries = map(pair, from, to, entries);
        logger.info("Finished reading TimeSeries {}", pair.technicalName());
        return timeSeries;
    }

    private String getFilename(Pair pair) {
        return pair.baseCurrency() + pair.counterCurrency() + "_" + pair.timeInMinutes() + ".csv";
    }
}
