package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.controller.SaleViewControllerImpl.SALE_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.TableSettingsFormControllerImpl.TABLE_SETTINGS_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;
import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.viewstate.TableViewState;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    private Popup tableSettingsForm;

    private RestaurantController restaurantController;

    private TableSettingsFormController tableSettingsFormController;

    private RestaurantServices restaurantServices;

    private RetailServices retailServices;

    private TableView tableView;

    private TableViewState tableViewState;

    @Inject
    public TableControllerImpl(RestaurantController restaurantController,
                               TableSettingsFormController tableSettingsFormController,
                               RestaurantServices restaurantServices,
                               RetailServices retailServices) {
        this.restaurantController = restaurantController;
        this.tableSettingsFormController = tableSettingsFormController;
        this.restaurantServices = restaurantServices;
        this.retailServices = retailServices;
    }

    public void setView(TableView tableView) {
        this.tableView = tableView;
        this.tableViewState = new TableViewState(restaurantController.getViewState(), tableView);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root, tableViewState);
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
    public ViewState getViewState() {
        return tableViewState;
    }

    @Override
    public void updateNode() {
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getTableNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getTableCapacity()));
        setTableBackgroundColor();
        showNode(root, tableView.getPosition());
    }

    @Override
    public void showTableSettingsForm(Control control) {
        tableSettingsForm = new Popup();
        tableSettingsForm.getContent().add(loadView(TABLE_SETTINGS_FORM_VIEW_PATH, tableSettingsFormController));
        tableSettingsFormController.loadTableSettings(this);

        Point2D point = calculatePopupPosition(control, (Pane)root.getParent());
        tableSettingsForm.show(root, point.getX(), point.getY());
    }

    @Override
    public void setTable(String name, int guestCount) {
        if (guestCount > tableView.getTableCapacity()) {
            ErrorMessage.showErrorMessage(root.getParent(), Resources.UI.getString("GuestCountTooHigh"));
            return;
        }
        restaurantServices.setTableName(tableView, name);
        restaurantServices.setTableGuestNumber(tableView, guestCount);
        tableSettingsForm.hide();
        updateNode();
    }

    @Override
    public void openTable(Control control) {
        retailServices.openTable(tableView);
        updateNode();
    }

    private void setTableBackgroundColor() {
        if(tableViewState.isOpen()) {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-background-color: #[0-9a-fA-F]*;)", "-fx-background-color: #33ff33;"));
        } else {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-background-color: #[0-9a-fA-F]*;)", "-fx-background-color: #ffffff;"));
        }
    }

    private void setTableBorderColor() {
        if(tableViewState.isSelected()) {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-border-color: #[0-9a-fA-F]*;)", "-fx-border-color: #00bfff;"));
        } else {
            vBox.setStyle(vBox.getStyle().replaceFirst("(-fx-border-color: #[0-9a-fA-F]*;)", "-fx-border-color: #000000;"));
        }
    }

    @Override
    public void deselectTable() {
        tableViewState.setSelected(false);
        setTableBorderColor();
    }

    @FXML
    public void onTableDragged(MouseEvent event) {
        tableViewState.setDragged(true);
    }

    @FXML
    public void onTableClicked(MouseEvent event) {
        if (isContextMenuOpen() || tableViewState.isDragged()) {
            tableViewState.setDragged(false);
            return;
        }
        if(tableViewState.isConfigurable()) {
            tableViewState.setSelected(!tableViewState.isSelected());
            restaurantController.selectTable(this, tableViewState.isSelected());
            setTableBorderColor();
        } else {
            if(!tableView.isOpen()) {
                return;
            }
            Parent root = (Parent) loadView(SALE_VIEW_PATH, getInjector().getInstance(SaleViewController.class));
            Main.getWindow().getScene().setRoot(root);
        }
    }

    private boolean isContextMenuOpen() {
        return root.getContextMenu() != null && root.getContextMenu().isShowing();
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
