package com.invoice.invoiceservice.commons;

import java.time.LocalDate;
import java.time.ZoneId;

public class DateHandler {

    private static final String TIMEZONE = "America/Recife";

    public static LocalDate getCurrentDateWithTimezone() {
        return LocalDate.now(ZoneId.of(TIMEZONE));
    }

    public static LocalDate createDateFromYearMonthDay(Integer year, Integer month, Integer day) {
        return LocalDate.of(year, month, day);
    }
}
