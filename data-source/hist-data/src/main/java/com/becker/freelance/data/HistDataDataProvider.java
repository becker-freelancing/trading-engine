package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipFile;

public class HistDataDataProvider extends DataProvider {

    private static final Logger logger = LoggerFactory.getLogger(HistDataDataProvider.class);

    @Override
    protected boolean supports(AppMode appMode) {
        return "HISTDATA".equalsIgnoreCase(appMode.getDataSourceName()) && appMode.isDemo();
    }

    @Override
    public TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException {
        logger.info("Start reading TimeSeries {}...", pair.technicalName());
        String filePath = PathUtil.fromRelativePath(".data-histdata\\" + getFilename(pair) + ".zip");

        // Read CSV from ZIP file
        ZipFile zipFile = new ZipFile(filePath);
        CSVReader reader = new CSVReader(new InputStreamReader(zipFile.getInputStream(zipFile.entries().nextElement())));

        List<String[]> rows = null;
        try {
            rows = reader.readAll();
        } catch (CsvException e) {
            throw new IOException(e);
        }
        TimeSeries timeSeries = map(pair, from, to, rows, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        logger.info("Finished reading TimeSeries {}", pair.technicalName());
        return timeSeries;
    }

    private String getFilename(Pair pair) {
        return pair.baseCurrency() + pair.counterCurrency() + "_" + pair.timeInMinutes() + ".csv";
    }
}
