package com.inspirationlogical.receipt.reserver.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import lombok.SneakyThrows;

public class DateParser {

    private static final String DATE_SEPARATOR = "-";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_PATTERN);

    private DateParser(){}

    @SneakyThrows
    public static LocalDate parseDate(String dateParameter) {
        Date date;
        List<String> dateParts = Arrays.asList(dateParameter.split(DATE_SEPARATOR));

        if (dateParts.size() < 3) {
            String dateString = LocalDate.now().getYear() + DATE_SEPARATOR;

            if (dateParts.size() < 2) {
                dateString += LocalDate.now().getMonthValue() + DATE_SEPARATOR;
            }

            date = DATE_FORMAT.parse(dateString + dateParameter);
        } else {
            date = DATE_FORMAT.parse(dateParameter);
        }

        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}
