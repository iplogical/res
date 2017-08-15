package com.inspirationlogical.receipt.waiter.utility;

import com.inspirationlogical.receipt.corelib.model.transaction.Functor;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Created by Bálint on 2017.04.06..
 */
public class ConfirmMessage {

    public static void showConfirmDialog(String title, Functor onOk) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, title, ButtonType.YES, ButtonType.NO);
        alert.setTitle("");
        alert.setHeaderText("");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            onOk.doIt();
        }
    }
}
