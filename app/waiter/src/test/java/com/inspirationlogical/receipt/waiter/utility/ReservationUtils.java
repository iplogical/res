package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.frontend.viewmodel.ReservationViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

import static com.inspirationlogical.receipt.waiter.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.waiter.utility.JavaFXIds.*;
import static org.junit.Assert.assertEquals;

public class ReservationUtils extends AbstractUtils {

    public static void backToRestaurantView() {
        clickButtonThenWait("Common.BackToRestaurantView", 500);
    }

    public static void addReservation(String name, int tableNumber, int guestCount) {
        setReservationName(name);
        setReservationTableNumber(tableNumber);
        setReservationGuestCount(guestCount);
        setReservationPhoneNumber("+36307654321");
        clickOnConfirm();
    }

    public static void setReservationName(String text) {
        setTextField(RESERVATIONS_NAME, text);
    }

    public static String getReservationName() {
        return getTextField(RESERVATIONS_NAME);
    }

    public static void setReservationTableNumber(int number) {
        setTextField(RESERVATIONS_TABLE_NUMBER, Integer.toString(number));
    }

    public static int getReservationTableNumber() {
        return Integer.valueOf(getTextField(RESERVATIONS_TABLE_NUMBER));
    }

    public static void setReservationGuestCount(int guestCount) {
        setTextField(RESERVATIONS_GUEST_COUNT, Integer.toString(guestCount));
    }

    public static String getReservationGuestCount() {
        return getTextField(RESERVATIONS_GUEST_COUNT);
    }

    public static void setReservationNote(String text) {
        setTextArea(RESERVATIONS_NOTE, text);
    }

    public static String getReservationNote() {
        return getTextArea(RESERVATIONS_NOTE);
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

    public static int getReservationGuestCount(int row) {
        return getReservations().get(row - 1).getGuestCountAsInt();
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
