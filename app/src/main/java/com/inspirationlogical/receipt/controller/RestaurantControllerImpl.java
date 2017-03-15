package com.inspirationlogical.receipt.controller;

import static com.inspirationlogical.receipt.controller.AddTableFormControllerImpl.ADD_TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.controller.ContextMenuControllerImpl.CONTEXT_MENU_VIEW_PATH;
import static com.inspirationlogical.receipt.controller.TableControllerImpl.TABLE_VIEW_PATH;
import static com.inspirationlogical.receipt.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.model.enums.TableType.VIRTUAL;
import static com.inspirationlogical.receipt.utility.PredicateOperations.and;
import static com.inspirationlogical.receipt.utility.PredicateOperations.not;
import static com.inspirationlogical.receipt.view.NodeUtility.getNodePosition;
import static com.inspirationlogical.receipt.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.view.ViewLoader.loadView;
import static com.inspirationlogical.receipt.view.ViewLoader.loadViewHidden;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.service.RestaurantServices;
import com.inspirationlogical.receipt.utility.Wrapper;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
    AnchorPane layout;

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
        initRestaurant();
        initTables();
        initContextMenu();
        initAddTableForm();
    }

    private void initRestaurant() {
        restaurantView = restaurantServices.getActiveRestaurant();
    }

    private void initTables() {
        restaurantServices.getTables(restaurantView).stream().filter(NORMAL_VISIBLE_TABLE).forEach(this::drawTable);
    }

    private void initContextMenu() {
        contextMenu = (VBox) loadViewHidden(CONTEXT_MENU_VIEW_PATH, contextMenuController);
        layout.getChildren().add(contextMenu);

        addPressAndHoldHandler(layout, Duration.millis(HOLD_DURATION_MILLIS));
    }

    private void initAddTableForm() {
        addTableForm = (VBox) loadViewHidden(ADD_TABLE_FORM_VIEW_PATH, addTableFormController);
        layout.getChildren().add(addTableForm);
    }

    private void addPressAndHoldHandler(Node node, Duration holdTime) {
        Wrapper<Point2D> positionWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            Point2D position = positionWrapper.getContent().add(getNodePosition(node));
            showNode(contextMenu, position);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            positionWrapper.setContent(new Point2D(event.getX(), event.getY()));
            holdTimer.playFromStart();
            contextMenu.setVisible(false);
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
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

        addPressAndHoldHandler(tableController.getView(), Duration.millis(HOLD_DURATION_MILLIS));

        layout.getChildren().add(tableController.getView());
    }
}
