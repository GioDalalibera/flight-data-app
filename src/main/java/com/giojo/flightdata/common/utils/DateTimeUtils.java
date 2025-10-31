package com.giojo.flightdata.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class DateTimeUtils {

    public static Instant startOfDayUtc(Instant dateTimeUtc) {
        return dateTimeUtc.atZone(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);
    }

    public static Instant startOfDayUtc(LocalDate localDate) {
        return localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    public static Instant endOfDayUtc(Instant dateTimeUtc) {
        return dateTimeUtc.atZone(ZoneOffset.UTC)
                .toLocalDateTime()
                .with(LocalTime.MAX)
                .toInstant(ZoneOffset.UTC);
    }

    public static Instant endOfDayUtc(LocalDate localDate) {
        return localDate.atStartOfDay().with(LocalTime.MAX).toInstant(ZoneOffset.UTC);
    }

}
