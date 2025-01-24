package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.Pair;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.commons.TimeSeries;
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

public class KrakenDataProvider extends DataProvider{

    private static final Logger logger = LoggerFactory.getLogger(KrakenDataProvider.class);

    @Override
    protected boolean supports(AppMode appMode) {
        return "KRAKEN".equalsIgnoreCase(appMode.getDataSourceName()) && appMode.isDemo();
    }

    @Override
    public TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException {
        logger.info("Start reading TimeSeries {}...", pair.getTechnicalName());
        String filePath = PathUtil.fromRelativePath(".data\\" + pair.getFilename() + ".zip");

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
        logger.info("Finished reading TimeSeries {}", pair.getTechnicalName());
        return timeSeries;

    }
}
