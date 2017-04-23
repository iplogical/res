package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
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
    private TextField tableName;

    @FXML
    private TextField guestCount;

    @FXML
    private TextArea note;

    @FXML
    TextField number;

    @FXML
    TextField capacity;

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
        guestCount.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        number.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        capacity.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        try {
            Integer tableNumber = Integer.valueOf(number.getText());
            Integer tableCapacity = Integer.valueOf(capacity.getText());
            Integer tableguestCount = guestCount.getText().equals("") ? 0 : Integer.valueOf(guestCount.getText());
            if (creation) {
                restaurantController.createTable(tableNumber, tableCapacity);
            } else {
                restaurantController.editTable(tableController, tableNumber, tableCapacity);
                tableController.setTable(tableName.getText(), tableguestCount, note.getText());
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
            number.setText(String.valueOf(tableView.getNumber()));
            capacity.setText(String.valueOf(tableView.getCapacity()));
            tableName.setText(tableView.getName());
            guestCount.setText(String.valueOf(tableView.getGuestCount()));
            note.setText(tableView.getNote());
        } else {
            creation = true;
            title.setText(resourceBundle.getString("TableForm.Create"));
            number.clear();
            capacity.clear();
            tableName.clear();
            guestCount.clear();
            note.clear();
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
