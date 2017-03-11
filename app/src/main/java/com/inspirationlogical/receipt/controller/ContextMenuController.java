package com.inspirationlogical.receipt.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

@Singleton
public class ContextMenuController implements Initializable {

    public static final String CONTEXT_MENU_VIEW_PATH = "/view/fxml/ContextMenu.fxml";

    @FXML
    VBox view;

    @FXML
    Button addTable;

    @FXML
    Button renameTable;

    @FXML
    Button deleteTable;

    @FXML
    Button mergeTables;

    @FXML
    Button splitTables;

    @Inject
    RestaurantController restaurantController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addTable.setUserData(new Boolean(true));
        renameTable.setUserData(new Boolean(false));
        deleteTable.setUserData(new Boolean(false));
        mergeTables.setUserData(new Boolean(true));
        splitTables.setUserData(new Boolean(true));
    }

    @FXML
    public void onAddTable(MouseEvent event) {

        hidePopup();

        Point2D position = new Point2D(event.getScreenX(), event.getScreenY());

        addNewTable(position);
    }

    @FXML
    public void onRenameTable(MouseEvent event) {
        hidePopup();
    }

    @FXML
    public void onDeleteTable(MouseEvent event) {
        hidePopup();
    }

    @FXML
    public void onMergeTables(MouseEvent event) {
        hidePopup();
    }

    @FXML
    public void onSplitTables(MouseEvent event) {
        hidePopup();
    }

    private void addNewTable(Point2D position) {
    }

    private void hidePopup() {
        restaurantController.getPopup().hide();
    }
}