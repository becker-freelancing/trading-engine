package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.Pair;
import com.becker.freelance.commons.PathUtil;
import com.becker.freelance.commons.TimeSeries;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.ZipFile;

public class KrakenDataProvider extends DataProvider{
    @Override
    protected boolean supports(AppMode appMode) {
        return AppMode.KRAKEN_DEMO.equals(appMode);
    }

    @Override
    public TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException {
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
        return map(pair, from, to, rows, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    }
}
