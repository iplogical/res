package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
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

    private static final String TABLE_FORM_VIEW_PATH = "/view/fxml/TableForm.fxml";
    private static final Integer TABLE_DEFAULT_CAPACITY = 4;
    private static final Integer TABLE_DEFAULT_WIDTH = 80;
    private static final Integer TABLE_DEFAULT_HEIGHT = 80;

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

    @FXML
    TextField width;

    @FXML
    TextField height;

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
        width.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        height.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
    }

    @FXML
    public void onConfirm(MouseEvent event) {
        try {
            Integer tableNumber = Integer.valueOf(number.getText());
            Integer tableCapacity = Integer.valueOf(capacity.getText());
            Integer tableGuestCount = guestCount.getText().equals("") ? 0 : Integer.valueOf(guestCount.getText());
            Dimension2D dimension = new Dimension2D(Integer.valueOf(width.getText()), Integer.valueOf(height.getText()));
            if (creation) {
                restaurantController.createTable(tableNumber, tableCapacity, dimension);
            } else {
                restaurantController.editTable(tableController, tableName.getText(), tableGuestCount, note.getText(),
                        tableNumber, tableCapacity, dimension);
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
            width.setText(String.valueOf((int) tableView.getDimension().getWidth()));
            height.setText(String.valueOf((int) tableView.getDimension().getHeight()));
        } else {
            creation = true;
            title.setText(resourceBundle.getString("TableForm.Create"));
            number.setText(String.valueOf(restaurantController.getFirstUnusedTableNumber()));
            capacity.setText(TABLE_DEFAULT_CAPACITY.toString());
            tableName.clear();
            guestCount.clear();
            note.clear();
            width.setText(TABLE_DEFAULT_WIDTH.toString());
            height.setText(TABLE_DEFAULT_HEIGHT.toString());
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
