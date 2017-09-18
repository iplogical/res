package com.inspirationlogical.receipt.waiter.controller.reservation;

import javafx.beans.value.ChangeListener;
import jfxtras.scene.control.CalendarPicker;
import lombok.Data;
import lombok.Getter;

import javax.ws.rs.GET;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Locale;

public class CalendarPickerWrapper {

    private @Getter CalendarPicker date;
    private @Getter LocalDate selectedDate;

    public static CalendarPickerWrapper getInstance() {
        CalendarPickerWrapper instance = new CalendarPickerWrapper();
        instance.initDate();
        instance.addDateListener((observable, oldValue, newValue) -> {
            instance.selectedDate = LocalDateTime.ofInstant(newValue.getTime().toInstant(), ZoneId.systemDefault()).toLocalDate();
        });
        return instance;
    }

    private CalendarPickerWrapper() {
        date = new CalendarPicker();
    }

    private void initDate() {
        date.setCalendar(Calendar.getInstance());
        selectedDate = LocalDate.now();
        date.setLocale(Locale.forLanguageTag("hu-HU"));
        date.setPrefWidth(400);
    }

    private void addDateListener(ChangeListener<? super Calendar> listener) {
        date.calendarProperty().addListener(listener);
    }
}
