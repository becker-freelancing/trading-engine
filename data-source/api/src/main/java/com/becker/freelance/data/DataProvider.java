package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.Pair;
import com.becker.freelance.commons.TimeSeries;
import com.becker.freelance.commons.TimeSeriesEntry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public abstract class DataProvider {

    public static DataProvider getInstance(AppMode appMode){
        ServiceLoader<DataProvider> serviceLoader = ServiceLoader.load(DataProvider.class);

        for (DataProvider dataProvider : serviceLoader) {
            if (dataProvider.supports(appMode)){
                return dataProvider;
            }
        }

        throw new IllegalArgumentException("AppMode " + appMode + " is not supported");
    }

    protected abstract boolean supports(AppMode appMode);

    public abstract TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException;

    protected TimeSeries map(Pair pair, LocalDateTime fromTime, LocalDateTime toTime, List<String[]> rows, DateTimeFormatter formatter) {

        Map<LocalDateTime, TimeSeriesEntry> dataList = rows.stream().skip(1).parallel().map(row -> {
                    LocalDateTime time = LocalDateTime.parse(row[0], formatter);
                    double open = Double.parseDouble(row[1]);
                    double high = Double.parseDouble(row[2]);
                    double low = Double.parseDouble(row[3]);
                    double close = Double.parseDouble(row[4]);
                    double volume = Double.parseDouble(row[5]);
                    double trades = Double.parseDouble(row[6]);
                    return new TimeSeriesEntry(
                            time, open, open, high, high, low, low,
                            close, close, volume, trades, pair
                    );
                })
                .collect(Collectors.toMap(TimeSeriesEntry::getTime, entry -> entry, (existing, replacement) -> existing));


        //FFill
        LocalDateTime firstBeforeFromTime = dataList.keySet().stream()
                .filter(time -> time.isBefore(fromTime))
                .max(Comparator.naturalOrder()).orElse(fromTime);
        TimeSeriesEntry currentEntry = dataList.get(firstBeforeFromTime);

        dataList = dataList.entrySet().stream().parallel()
                .filter(entry -> entry.getKey().isAfter(fromTime) && entry.getKey().isBefore(toTime))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        LocalDateTime currentTime = firstBeforeFromTime;

        while (currentTime.isBefore(toTime) || currentTime.isEqual(toTime)){
            currentTime = currentTime.plus(pair.toDuration());
            if (dataList.containsKey(currentTime)){
                currentEntry = dataList.get(currentTime);
            } else {
                dataList.put(currentTime, new TimeSeriesEntry(
                        currentTime,
                        currentEntry.getOpenBid(), currentEntry.getOpenAsk(),
                        currentEntry.getHighBid(), currentEntry.getHighAsk(),
                        currentEntry.getLowBid(), currentEntry.getLowAsk(),
                        currentEntry.getCloseBid(), currentEntry.getCloseAsk(),
                        currentEntry.getVolume(), currentEntry.getTrades(), currentEntry.getPair()
                ));
            }
        }


        // Create TimeSeries object and return
        return new TimeSeries(pair, dataList);
    }

}
