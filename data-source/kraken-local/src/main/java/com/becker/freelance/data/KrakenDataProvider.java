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

public class KrakenDataProvider extends DataProvider{

    private static final Logger logger = LoggerFactory.getLogger(KrakenDataProvider.class);
    private static final RowMappingInfo MAPPING_INFO = new RowMappingInfo(true, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 6);

    private Pair pair;

    public KrakenDataProvider(Pair pair) {
        this.pair = pair;
    }

    protected Map<LocalDateTime, TimeSeriesEntry> readCsvContent(InputStream fileInputStream) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return new CsvDataReader(MAPPING_INFO, fileInputStream, formatter, pair).readCsvParallel()
                .collect(Collectors.toMap(TimeSeriesEntry::time, entry -> entry, (existing, replacement) -> existing));

    }

    @Override
    public TimeSeries readTimeSeries(LocalDateTime from, LocalDateTime to) {
        logger.info("Start reading TimeSeries {}...", pair.technicalName());
        String filePath = PathUtil.fromRelativePath("data\\" + getFilename() + ".zip");

        Map<LocalDateTime, TimeSeriesEntry> entries;

        // Read CSV from ZIP file
        try (ZipFile zipFile = new ZipFile(filePath)) {
            InputStream inputStream = zipFile.getInputStream(zipFile.entries().nextElement());

            entries = readCsvContent(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read TimeSeries for " + pair, e);
        }

        TimeSeries timeSeries = map(pair, from, to, entries);
        logger.info("Finished reading TimeSeries {}", pair.technicalName());
        return timeSeries;

    }

    private String getFilename() {
        return pair.baseCurrency() + pair.counterCurrency() + "_" + pair.timeInMinutes() + ".csv";
    }
}
