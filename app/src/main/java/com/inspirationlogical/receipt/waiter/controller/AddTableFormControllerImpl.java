package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;
import static java.lang.Integer.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

@Singleton
public class AddTableFormControllerImpl implements AddTableFormController {

    public static final String ADD_TABLE_FORM_VIEW_PATH = "/view/fxml/AddTableForm.fxml";

    @FXML
    VBox view;

    @FXML
    TextField number;

    @FXML
    TextField capacity;

    @FXML
    CheckBox virtual;

    @FXML
    Button create;

    private RestaurantController restaurantController;

    @Inject
    public AddTableFormControllerImpl(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(view);
        number.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        capacity.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @FXML
    public void onCreate(MouseEvent event) {
        try {
            Integer tableNumber = valueOf(number.getText());
            Integer tableCapacity = valueOf(capacity.getText());
            boolean isVirtual = virtual.isSelected();
            restaurantController.createTable(tableNumber, tableCapacity, isVirtual);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onCancel(MouseEvent event) {
        hideNode(view);
    }
}
