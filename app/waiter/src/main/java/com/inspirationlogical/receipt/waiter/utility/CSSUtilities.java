package com.inspirationlogical.receipt.waiter.utility;

import javafx.scene.layout.VBox;

/**
 * Created by Bálint on 2017.03.24..
 */
public class CSSUtilities {

    public static void setBorderColor(boolean isSelected, VBox vBox) {
        if(isSelected) {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-border-color: #[0-9a-fA-F]*;)", "-fx-border-color: #00bfff;"));
        } else {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-border-color: #[0-9a-fA-F]*;)", "-fx-border-color: #000000;"));
        }
    }

    public static void setBackgroundColor(boolean isOpen, VBox vBox) {
        if(isOpen) {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-background-color: #[0-9a-fA-F]*;)", "-fx-background-color: #33ff33;"));
        } else {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-background-color: #[0-9a-fA-F]*;)", "-fx-background-color: #ffffff;"));
        }
    }
}
