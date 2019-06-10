package com.inspirationlogical.receipt.corelib.utility;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import javafx.geometry.Pos;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class NotificationMessage {

    private static void showMessage(Object owner, String title, String text, int seconds) {
        Notifications.create()
                .title(title)
                .text(text)
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(seconds))
                .owner(owner)
                .darkStyle()
                .show();
    }

    private static void showErrorMessage(Object owner, String text, int seconds) {
        showMessage(owner, Resources.CONFIG.getString("ErrorMessage"), text, seconds);
    }

    public static void showErrorMessage(Object owner, String text) {
        showErrorMessage(owner, text, 5);
    }

    public static void showSaveSuccessfulMessage(Object owner, String text) {
        showMessage(owner, Resources.CONFIG.getString("SaveSuccessfulMessage"), text, 5);
    }

    public static void showErrorMessageLong(Object owner, String text) {
        showErrorMessage(owner, text, 300);
    }
}
