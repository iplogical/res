package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

@Singleton
public class ContextMenuControllerImpl implements ContextMenuController {

    public static final String CONTEXT_MENU_VIEW_PATH = "/view/fxml/ContextMenu.fxml";

    @FXML
    VBox node;

    @FXML
    Button addTable;

    @FXML
    Button editTable;

    @FXML
    Button deleteTable;

    @FXML
    Button renameTable;

    @FXML
    Button mergeTables;

    @FXML
    Button splitTables;

    private RestaurantController restaurantController;

    private Node source;

    @Inject
    public ContextMenuControllerImpl(RestaurantController restaurantController) {
        this.restaurantController = restaurantController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(node);
    }

    @Override
    public void setSourceNode(Node node) {
        this.source = node;
    }

    @FXML
    public void onAddTable(MouseEvent event) {
        hidePopup();
        restaurantController.showAddTableForm();
    }

    @FXML
    public void onEditTable(MouseEvent event) {
        hidePopup();
        restaurantController.showEditTableForm(source);
    }

    @FXML
    public void onDeleteTable(MouseEvent event) {
        hidePopup();
        restaurantController.deleteTable(source);
    }

    @FXML
    public void onRenameTable(MouseEvent event) {
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
        node.setVisible(false);
    }
}
