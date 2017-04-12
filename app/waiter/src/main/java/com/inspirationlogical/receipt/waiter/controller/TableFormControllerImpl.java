package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    Label title;

    @FXML
    TextField number;

    @FXML
    TextField capacity;

    @FXML
    Button confirm;

    private boolean creation;

    private TableController tableController;

    private RestaurantController restaurantController;

    private ResourceBundle resourceBundle;

    @Inject
    public TableFormControllerImpl(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        resourceBundle = resources;
        addDragAndDrop(root);
        number.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        capacity.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        try {
            Integer tableNumber = Integer.valueOf(number.getText());
            Integer tableCapacity = Integer.valueOf(capacity.getText());
            if (creation) {
                restaurantController.createTable(tableNumber, tableCapacity);
            } else {
                restaurantController.editTable(tableController, tableNumber, tableCapacity);
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
            title.setText(resourceBundle.getString("TableForm.Edit"));
            TableView tableView = tableController.getView();
            number.setText(String.valueOf(tableView.getTableNumber()));
            capacity.setText(String.valueOf(tableView.getTableCapacity()));
        } else {
            creation = true;
            title.setText(resourceBundle.getString("TableForm.Create"));
            number.clear();
            capacity.clear();
        }
    }

    @Override
    public String getViewPath() {
        return TABLE_FORM_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
