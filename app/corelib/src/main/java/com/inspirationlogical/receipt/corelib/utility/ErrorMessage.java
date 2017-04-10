package com.inspirationlogical.receipt.corelib.utility;

import org.controlsfx.control.Notifications;

import com.inspirationlogical.receipt.corelib.utility.Resources;

import javafx.geometry.Pos;
import javafx.util.Duration;

/**
 * Created by Bálint on 2017.03.21..
 */
public class ErrorMessage {

    public static void showErrorMessage(Object owner, String text) {
        Notifications.create()
                .title(Resources.CONFIG.getString("ErrorMessage"))
                .text(text)
                .position(Pos.CENTER)
                .hideAfter(Duration.seconds(5))
                .owner(owner)
                .darkStyle()
                .show();
    }
}
