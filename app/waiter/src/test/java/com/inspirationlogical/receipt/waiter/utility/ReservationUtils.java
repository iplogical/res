package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickButtonThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.clickOnThenWait;
import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static org.junit.Assert.assertEquals;

public class ReservationUtils extends AbstractUtils {

    public static void backToRestaurantView() {
        clickButtonThenWait("Common.BackToRestaurantView", 500);
    }

    public static void setReservationName(String text) {
        setTextField(RESERVATIONS_NAME, text);
    }

    public static void setReservationTableNumber(int number) {
        setTextField(RESERVATIONS_TABLE_NUMBER, Integer.toString(number));
    }

    public static void setReservationGuestCount(int guestCount) {
        setTextField(RESERVATIONS_GUEST_COUNT, Integer.toString(guestCount));
    }

    public static void setReservationPhoneNumber(String text) {
        setTextField(RESERVATIONS_PHONE_NUMBER, text);
    }

    public static void clickOnReservation(int row) {
        clickOnThenWait(SOLD_POINTS.get(row - 1), 50);
    }

    public static void clickOnConfirm() {
        clickButtonThenWait(RESERVATIONS_CONFIRM, 100);
    }

    public static void clickOnUpdate() {
        clickButtonThenWait(RESERVATIONS_UPDATE, 100);
    }

    public static void clickOnDelete() {
        clickButtonThenWait(RESERVATIONS_DELETE, 100);
    }

    public static void clickOnOpenTable() {
        clickButtonThenWait(RESERVATIONS_OPEN_TABLE, 100);
    }

    public static String getReservationName(int row) {
        return getReservations().get(row - 1).getName();
    }

    public static int getReservationTableNumber(int row) {
        return getReservations().get(row - 1).getTableNumberAsInt();
    }

    public static String getReservationDate(int row) {
        return getReservations().get(row - 1).getDate();
    }

    public static String getReservationStartTime(int row) {
        return getReservations().get(row - 1).getStartTime();
    }

    public static String getReservationEndTime(int row) {
        return getReservations().get(row - 1).getEndTime();
    }

    public static void assertNoReservation() {
        assertEquals(0, getReservations().size());
    }

    public static void assertNumberOfReservations(int number) {
        assertEquals(number, getReservations().size());
    }

    private static ObservableList<ReservationViewModel> getReservations() {
        TableView<ReservationViewModel> tableView = robot.find(RESERVATIONS_TABLE);
        return tableView.getItems();
    }
}
