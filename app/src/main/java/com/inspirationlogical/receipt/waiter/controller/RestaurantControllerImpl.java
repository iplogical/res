package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.controller.AddTableFormControllerImpl.ADD_TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.ContextMenuControllerImpl.CONTEXT_MENU_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.TableControllerImpl.TABLE_VIEW_PATH;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.VIRTUAL;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.and;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.not;
import static com.inspirationlogical.receipt.waiter.view.DragAndDropHandler.setEnableControl;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.getNodePosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.waiter.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadViewHidden;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

@Singleton
public class RestaurantControllerImpl implements RestaurantController {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    private static final int HOLD_DURATION_MILLIS = 500;
    private static Predicate<TableView> NORMAL_TABLE = not(TableView::isVirtual);
    private static Predicate<TableView> VISIBLE_TABLE = TableView::isVisible;
    private static Predicate<TableView> NORMAL_VISIBLE_TABLE = and(NORMAL_TABLE, VISIBLE_TABLE);

    @FXML
    AnchorPane tables;

    @FXML
    AnchorPane virtual;

    @FXML
    Button consumption;

    @FXML
    Button reservation;

    @FXML
    ToggleButton configuration;

    private VBox contextMenu;

    private VBox addTableForm;

    private ContextMenuController contextMenuController;

    private AddTableFormController addTableFormController;

    private RestaurantServices restaurantServices;

    private RestaurantView restaurantView;

    @Inject
    public RestaurantControllerImpl(RestaurantServices restaurantServices, ContextMenuController contextMenuController,
                                    AddTableFormController addTableFormController) {
        this.contextMenuController = contextMenuController;
        this.restaurantServices = restaurantServices;
        this.addTableFormController = addTableFormController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu();
        initAddTableForm();
        initRestaurant();
        initTables();
        setEnableControl(configuration);
    }

    private void initRestaurant() {
        restaurantView = restaurantServices.getActiveRestaurant();
    }

    private void initTables() {
        restaurantServices.getTables(restaurantView).stream().filter(VISIBLE_TABLE).forEach(this::drawTable);
    }

    private void initContextMenu() {
        contextMenu = (VBox) loadViewHidden(CONTEXT_MENU_VIEW_PATH, contextMenuController);
        tables.getChildren().add(contextMenu);

        addPressAndHold(tables, contextMenu, Duration.millis(HOLD_DURATION_MILLIS));
    }

    private void initAddTableForm() {
        addTableForm = (VBox) loadViewHidden(ADD_TABLE_FORM_VIEW_PATH, addTableFormController);
        tables.getChildren().add(addTableForm);
    }

    @Override
    public void showAddTableForm() {
        Point2D position = getNodePosition(contextMenu);

        showNode(addTableForm, position);
    }

    @Override
    public void createTable(int number, int capacity, boolean isVirtual) {
        Point2D position = getNodePosition(addTableForm);
        TableType tableType = isVirtual ? VIRTUAL : NORMAL;

        TableView tableView = restaurantServices.addTable(restaurantView, tableType, number);

        restaurantServices.setTableCapacity(tableView, capacity);
        restaurantServices.moveTable(tableView, position);

        hideNode(addTableForm);
        drawTable(tableView);
    }

    private void drawTable(TableView tableView) {
        TableController tableController = new TableControllerImpl(tableView);

        loadView(TABLE_VIEW_PATH, tableController);

        addPressAndHold(tableController.getView(), contextMenu, Duration.millis(HOLD_DURATION_MILLIS));

        Pane pane = tableView.isVirtual() ? virtual : tables;

        pane.getChildren().add(tableController.getView());
    }
}
