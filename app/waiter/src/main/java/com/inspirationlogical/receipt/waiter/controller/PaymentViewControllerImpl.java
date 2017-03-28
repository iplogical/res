package com.inspirationlogical.receipt.waiter.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bálint on 2017.03.28..
 */
public class PaymentViewControllerImpl implements PaymentViewController {

    public static final String PAYMENT_VIEW_PATH = "/view/fxml/PaymentView.fxml";

    @FXML
    private BorderPane root;

    @Override
    public Node getRootNode() {
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
