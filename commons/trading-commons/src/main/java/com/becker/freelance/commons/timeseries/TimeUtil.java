package com.becker.freelance.commons.timeseries;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtil {

    private static final LocalDateTime WEEK_REFERENCE = LocalDateTime.of(1999, 12, 27, 0, 0);

    public static boolean isAligned(LocalDateTime time, Duration duration) {
        // Für Wochen: 7 Tage
        if (duration.toDays() % 7 == 0 && duration.toDays() >= 7) {
            long secondsSinceRef = Duration.between(WEEK_REFERENCE, time).getSeconds();
            return secondsSinceRef % duration.getSeconds() == 0;
        }

        // Für ganze Tage
        if (duration.toDays() >= 1) {
            LocalDateTime midnight = time.toLocalDate().atStartOfDay();
            long secondsSinceMidnight = Duration.between(midnight, time).getSeconds();
            return secondsSinceMidnight % duration.getSeconds() == 0;
        }

        // Für Stunden/Minuten/etc. innerhalb eines Tages
        LocalDateTime dayStart = time.toLocalDate().atStartOfDay();
        long secondsSinceDayStart = Duration.between(dayStart, time).getSeconds();
        return secondsSinceDayStart % duration.getSeconds() == 0;
    }

    public static LocalDateTime nextAligned(LocalDateTime dateTime, Duration timeShift) {
        LocalDateTime localDateTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), dateTime.getHour(), dateTime.getMinute());

        while (!isAligned(localDateTime, timeShift)) {
            localDateTime = localDateTime.plusMinutes(1);
        }

        return localDateTime;
    }
}
