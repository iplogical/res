package com.inspirationlogical.receipt.corelib.utility;

import org.controlsfx.control.Notifications;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import javafx.geometry.Pos;
import javafx.util.Duration;

public class ErrorMessage {

    private static void showErrorMessage(Object owner, String text, int seconds) {
        Notifications.create()
                .title(Resources.CONFIG.getString("ErrorMessage"))
                .text(text)
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(seconds))
                .owner(owner)
                .darkStyle()
                .show();
    }

    public static void showErrorMessage(Object owner, String text) {
        showErrorMessage(owner, text, 5);
    }

    public static void showErrorMessageLong(Object owner, String text) {
        showErrorMessage(owner, text, 300);
    }
}
