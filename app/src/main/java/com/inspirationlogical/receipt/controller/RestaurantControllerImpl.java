package com.inspirationlogical.receipt.controller;

import static com.inspirationlogical.receipt.controller.ContextMenuControllerImpl.CONTEXT_MENU_VIEW_PATH;
import static com.inspirationlogical.receipt.controller.TableControllerImpl.TABLE_VIEW_PATH;
import static com.inspirationlogical.receipt.utility.PredicateOperations.and;
import static com.inspirationlogical.receipt.utility.PredicateOperations.not;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.model.enums.TableType;
import com.inspirationlogical.receipt.model.view.RestaurantView;
import com.inspirationlogical.receipt.model.view.TableView;
import com.inspirationlogical.receipt.registry.FXMLLoaderProvider;
import com.inspirationlogical.receipt.service.RestaurantServices;
import com.inspirationlogical.receipt.utility.Wrapper;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private ContextMenuController contextMenuController;

    private RestaurantServices restaurantServices;

    private RestaurantView restaurantView;

    @Inject
    public RestaurantControllerImpl(ContextMenuController contextMenuController, RestaurantServices restaurantServices) {
        this.contextMenuController = contextMenuController;
        this.restaurantServices = restaurantServices;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initRestaurant();
        initTables();
        initContextMenu();
    }

    private void initRestaurant() {
        restaurantView = restaurantServices.getActiveRestaurant();
    }

    private void initTables() {
        restaurantServices.getTables(restaurantView).stream().filter(NORMAL_TABLE).forEach(this::drawTable);
    }

    private void initContextMenu() {
        try {
            FXMLLoader loader = FXMLLoaderProvider.getLoader(CONTEXT_MENU_VIEW_PATH);
            loader.setController(contextMenuController);
            contextMenu = loader.load();
            contextMenu.setVisible(false);
            layout.getChildren().add(contextMenu);

            addPressAndHoldHandler(layout, Duration.millis(HOLD_DURATION_MILLIS));
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addPressAndHoldHandler(Node node, Duration holdTime) {

        Wrapper<MouseEvent> eventWrapper = new Wrapper<>();
        PauseTransition holdTimer = new PauseTransition(holdTime);

        holdTimer.setOnFinished(event -> {
            contextMenu.setLayoutX(eventWrapper.getContent().getX() + node.getLayoutX());
            contextMenu.setLayoutY(eventWrapper.getContent().getY() + node.getLayoutY());
            contextMenu.toFront();
            contextMenu.setVisible(true);
        });

        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            eventWrapper.setContent(event);
            holdTimer.playFromStart();
            contextMenu.setVisible(false);
        });

        node.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> holdTimer.stop());

        node.addEventHandler(MouseEvent.DRAG_DETECTED, event -> holdTimer.stop());
    }

    public void addTable() {

        Point2D position = new Point2D(contextMenu.getLayoutX(), contextMenu.getLayoutY());

        TableView tableView = restaurantServices.addTable(restaurantView, TableType.NORMAL, new Random(1).nextInt(10));

        restaurantServices.moveTable(tableView, position);

        drawTable(tableView);
    }

    private void drawTable(TableView tableView) {

        FXMLLoader loader = FXMLLoaderProvider.getLoader(TABLE_VIEW_PATH);
        TableController tableController = new TableControllerImpl(tableView);
        loader.setController(tableController);

        try {
            loader.load();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        addPressAndHoldHandler(tableController.getView(), Duration.millis(HOLD_DURATION_MILLIS));

        layout.getChildren().add(tableController.getView());
    }
}
