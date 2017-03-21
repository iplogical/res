package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.controller.ConfigureTableFormControllerImpl.CONFIGURE_TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl.LAYOUT_OFFSET_Y;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;
import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.TableView;

import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import javafx.fxml.FXML;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Popup;

public class TableControllerImpl implements TableController {

    public static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static double TABLE_WIDTH = 100.0;
    private static double TABLE_HEIGHT = 100.0;

    @FXML
    AnchorPane tablesTab;

    @FXML
    Label root;

    @FXML
    VBox vBox;

    @FXML
    Label name;

    @FXML
    Label number;

    @FXML
    Label guests;

    @FXML
    Label capacity;

    private Popup renameTableForm;

    private ConfigureTableFormController renameTableFormController;

    private RestaurantServices restaurantServices;

    private TableView tableView;

    private Toggle dragControl;

    @Inject
    public TableControllerImpl(RestaurantServices restaurantServices, TableView tableView, Toggle dragControl) {
        this.restaurantServices = restaurantServices;
        this.tableView = tableView;
        this.dragControl = dragControl;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root, dragControl);
        initVisual();
        updateNode();
    }

    private void initVisual() {
        root.setMinWidth(TABLE_WIDTH);
        root.setMinHeight(TABLE_HEIGHT);
    }

    @Override
    public TableView getView() {
        return tableView;
    }

    @Override
    public Control getRoot() {
        return root;
    }

    @Override
    public void updateNode() {
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getTableNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getTableCapacity()));
        setOpenTableBackgroundColor();
        showNode(root, tableView.getPosition());
    }

    @Override
    public void showConfigureTableForm(Control control) {
        renameTableFormController = new ConfigureTableFormControllerImpl(this);
        renameTableForm = new Popup();
        renameTableForm.getContent().add(loadView(CONFIGURE_TABLE_FORM_VIEW_PATH, renameTableFormController));
        renameTableFormController.loadConfigureTable(this);

        renameTableForm.show(root, tableView.getPosition().getX(), tableView.getPosition().getY() + LAYOUT_OFFSET_Y);
    }

    @Override
    public void configureTable(String name, int guestNumber) {
        tableView = restaurantServices.setTableName(tableView, name);
        tableView = restaurantServices.setTableGuestNumber(tableView, guestNumber);
        updateNode();
    }

    private void setOpenTableBackgroundColor() {
        if(tableView.isOpen()) {
            //TODO: Find a way to change the background color separetely.
            vBox.setStyle("-fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-color: #42e01a; -fx-background-radius: 10;");
        }
    }
}
