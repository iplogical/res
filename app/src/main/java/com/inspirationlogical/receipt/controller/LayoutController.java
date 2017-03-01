package com.inspirationlogical.receipt.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javax.inject.Inject;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class LayoutController implements Initializable {

    @FXML
    AnchorPane layout;

    @FXML
    Label test;

    @Inject
    private String title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        test.setText("Test!");
    }
}
