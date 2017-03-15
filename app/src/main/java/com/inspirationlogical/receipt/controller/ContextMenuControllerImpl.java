package com.inspirationlogical.receipt.controller;

import static com.inspirationlogical.receipt.view.DragAndDropHandler.addDragAndDrop;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

@Singleton
public class ContextMenuControllerImpl implements ContextMenuController {

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

    private RestaurantController restaurantController;

    @Inject
    public ContextMenuControllerImpl(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(view);
    }

    @FXML
    public void onAddTable(MouseEvent event) {
        hidePopup();
        restaurantController.showAddTableForm();
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

    private void hidePopup() {
        view.setVisible(false);
    }
}
