package com.becker.freelance.data;

import com.becker.freelance.commons.AppMode;
import com.becker.freelance.commons.pair.Pair;
import com.becker.freelance.commons.timeseries.TimeSeries;
import com.becker.freelance.commons.timeseries.TimeSeriesEntry;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public abstract class DataProvider {

    public static DataProvider getInstance(AppMode appMode){
        ServiceLoader<DataProvider> serviceLoader = ServiceLoader.load(DataProvider.class);
        List<DataProvider> providers = serviceLoader.stream().map(ServiceLoader.Provider::get).filter(provider -> provider.supports(appMode)).toList();

        if (providers.size() > 1){
            throw new IllegalStateException("Found multiple DataProvider for AppMode " + appMode.getDescription() + ": " + providers);
        }
        if (providers.isEmpty()) {
            throw new IllegalArgumentException("AppMode " + appMode + " is not supported");
        }

        return providers.get(0);
    }

    protected abstract boolean supports(AppMode appMode);

    public abstract TimeSeries readTimeSeries(Pair pair, LocalDateTime from, LocalDateTime to) throws IOException;

    protected TimeSeries map(Pair pair, LocalDateTime fromTime, LocalDateTime toTime, Map<LocalDateTime, TimeSeriesEntry> dataList) {


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
                        currentEntry.openBid(), currentEntry.openAsk(),
                        currentEntry.highBid(), currentEntry.highAsk(),
                        currentEntry.lowBid(), currentEntry.lowAsk(),
                        currentEntry.closeBid(), currentEntry.closeAsk(),
                        currentEntry.volume(), currentEntry.trades(), currentEntry.pair()
                ));
            }
        }


        // Create TimeSeries object and return
        return new TimeSeries(pair, dataList);
    }

}
