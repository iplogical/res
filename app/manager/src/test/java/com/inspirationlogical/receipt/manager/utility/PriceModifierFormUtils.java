package com.inspirationlogical.receipt.manager.utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.selectChoiceBoxItem;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.setDatePicker;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;

public class PriceModifierFormUtils  extends AbstractUtils{

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd.");
    public static void setName(String name) {
        setTextField(PRICE_MODIFIER_NAME, name);
    }

    public static void setOwnerProduct(int number) {
        selectChoiceBoxItem(PRICE_MODIFIER_OWNER_PRODUCT, number);
    }

    public static void setOwnerCategory(int number) {
        selectChoiceBoxItem(PRICE_MODIFIER_OWNER_CATEGORY, number - 1);
    }

    public static void setIsCategory(boolean isCategory) {

    }

    public static void setType(int number) {
        selectChoiceBoxItem(PRICE_MODIFIER_TYPE, number);
    }

    public static void setQuantityLimit(int quantityLimit) {
        setTextField(PRICE_MODIFIER_QUANTITY_LIMIT, String.valueOf(quantityLimit));
    }

    public static void setDiscountPercent(double discountPercent) {
        setTextField(PRICE_MODIFIER_DISCOUNT_PERCENT, String.valueOf(discountPercent));

    }

    public static void setStartDate(LocalDate startDate) {
        setDatePicker(PRICE_MODIFIER_START_DATE, startDate.format(DATE_FORMATTER));
    }

    public static void setEndDate(LocalDate endDate) {
        setDatePicker(PRICE_MODIFIER_END_DATE, endDate.format(DATE_FORMATTER));
    }

    public static void setRepeatPeriod(int number) {
        selectChoiceBoxItem(PRICE_MODIFIER_REPEAT_PERIOD, number);
    }

    public static void setRepeatPeriodMultiplier(int number) {
        setTextField(PRICE_MODIFIER_REPEAT_PERIOD_MULTIPLIER, String.valueOf(number));
    }

    public static void setDayOfWeek(int number) {
        selectChoiceBoxItem(PRICE_MODIFIER_DAY_OF_WEEK, number);
    }

    public static void setStartTime(LocalTime startTime) {
        setTextField(PRICE_MODIFIER_START_TIME_HOUR, String.valueOf(startTime.getHour()));
        setTextField(PRICE_MODIFIER_START_TIME_MINUTE, String.valueOf(startTime.getMinute()));
    }

    public static void setEndTime(LocalTime endTime) {
        setTextField(PRICE_MODIFIER_END_TIME_HOUR, String.valueOf(endTime.getHour()));
        setTextField(PRICE_MODIFIER_END_TIME_MINUTE, String.valueOf(endTime.getMinute()));
    }
}
