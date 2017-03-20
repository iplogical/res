package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

@Singleton
public class TableFormControllerImpl implements TableFormController {

    public static final String TABLE_FORM_VIEW_PATH = "/view/fxml/TableForm.fxml";

    @FXML
    VBox root;

    @FXML
    TextField number;

    @FXML
    TextField capacity;

    @FXML
    CheckBox virtual;

    @FXML
    Button confirm;

    private boolean creation;

    private TableController tableController;

    private RestaurantController restaurantController;

    @Inject
    public TableFormControllerImpl(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root, null);
        number.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        capacity.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        try {
            Integer tableNumber = Integer.valueOf(number.getText());
            Integer tableCapacity = Integer.valueOf(capacity.getText());
            boolean isVirtual = virtual.isSelected();
            if (creation) {
                restaurantController.createTable(tableNumber, tableCapacity, isVirtual);
            } else {
                restaurantController.editTable(tableController, tableNumber, tableCapacity, isVirtual);
            }
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void onCancel(MouseEvent event) {
        hideNode(root);
    }

    @Override
    public void loadTable(TableController tableController) {
        this.tableController = tableController;
        if (tableController != null) {
            creation = false;
            TableView tableView = tableController.getView();
            number.setText(String.valueOf(tableView.getTableNumber()));
            capacity.setText(String.valueOf(tableView.getTableCapacity()));
            virtual.setSelected(tableView.isVirtual());
        } else {
            creation = true;
            number.clear();
            capacity.clear();
            virtual.setSelected(false);
        }
    }
}
