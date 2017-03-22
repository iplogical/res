package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.application.Main.APP_HEIGHT;
import static com.inspirationlogical.receipt.waiter.application.Main.APP_WIDTH;
import static com.inspirationlogical.receipt.waiter.controller.ConfigureTableFormControllerImpl.CONFIGURE_TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.RestaurantControllerImpl.RESTAURANT_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.SaleViewControllerImpl.SALE_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;
import static java.lang.String.valueOf;

import java.net.URL;
import java.util.ResourceBundle;

import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.application.Main;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;
import com.inspirationlogical.receipt.waiter.viewstate.TableViewState;
import com.inspirationlogical.receipt.waiter.viewstate.ViewState;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    private Popup configureTableForm;

    private ConfigureTableFormController configureTableFormController;

    private RestaurantServices restaurantServices;

    private RetailServices retailServices;

    private TableView tableView;

    private TableViewState tableViewState;

    public TableControllerImpl(RestaurantServices restaurantServices,
                               RetailServices retailServices,
                               TableView tableView,
                               RestaurantViewState restaurantViewState,
                               ConfigureTableFormController configureTableFormController) {
        this.restaurantServices = restaurantServices;
        this.retailServices = retailServices;
        this.tableView = tableView;
        this.tableViewState = new TableViewState();
        this.tableViewState.setRestaurantViewState(restaurantViewState);
        this.configureTableFormController = configureTableFormController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(root, tableViewState.getRestaurantViewState());
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
        tableViewState.setOpen(tableView.isOpen());
        setTableBackgroundColor(tableViewState.isOpen());
        showNode(root, tableView.getPosition());
    }

    @Override
    public void showConfigureTableForm(Control control) {
        configureTableForm = new Popup();
        configureTableForm.getContent().add(loadView(CONFIGURE_TABLE_FORM_VIEW_PATH, configureTableFormController));
        configureTableFormController.loadConfigureTable(this);

        Point2D point = calculatePopupPosition(control, (Pane)root.getParent());
        configureTableForm.show(root, point.getX(), point.getY());
    }

    @Override
    public void configureTable(String name, int guestNumber) {
        restaurantServices.setTableName(tableView, name);
        restaurantServices.setTableGuestNumber(tableView, guestNumber);
        updateNode();
    }

    @Override
    public void openTable(Control control) {
        retailServices.openTable(tableView);
        updateNode();
    }

    private void setTableBackgroundColor(boolean isOpen) {
        if(isOpen) {
            //TODO: Find a way to change the background color separately.
            vBox.setStyle("-fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-color: #42e01a; -fx-background-radius: 10;");
        } else {
            vBox.setStyle("-fx-border-color: #000000; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-color: #ffffff; -fx-background-radius: 10;");
        }
    }

    @FXML
    public void onTableClicked(MouseEvent event) {
        if(!tableView.isOpen()) {
            return;
        }
        Parent root = (Parent) loadView(SALE_VIEW_PATH, getInjector().getInstance(SaleViewController.class));
        Main.getWindow().setScene(new Scene(root, APP_WIDTH, APP_HEIGHT));
        Main.getWindow().setFullScreen(true);
    }
}
