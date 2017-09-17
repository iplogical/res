package com.inspirationlogical.receipt.waiter.controller.reservation;

import javafx.beans.value.ChangeListener;
import jfxtras.scene.control.CalendarPicker;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;

@Data
public class CalendarPickerWrapper {

    private CalendarPicker date;
    private LocalDate selectedDate;

    public CalendarPickerWrapper() {
        date = new CalendarPicker();
    }

    public void initDate() {
        date.setCalendar(Calendar.getInstance());
        selectedDate = LocalDate.now();
        date.setLocale(Locale.forLanguageTag("hu-HU"));
        date.setPrefWidth(400);
    }

    public void addDateListener(ChangeListener<? super Calendar> listener) {
        date.calendarProperty().addListener(listener);
    }
}
