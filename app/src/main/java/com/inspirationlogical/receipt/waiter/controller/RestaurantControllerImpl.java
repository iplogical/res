package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.VIRTUAL;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.and;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.not;
import static com.inspirationlogical.receipt.waiter.controller.TableControllerImpl.TABLE_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl.TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.getNodePosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.waiter.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadViewHidden;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.waiter.builder.RestaurantContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.builder.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.exception.ViewNotFoundException;
import com.inspirationlogical.receipt.waiter.view.DragAndDropHandler;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    Label tables;

    @FXML
    AnchorPane virtual;

    @FXML
    Button consumption;

    @FXML
    Button reservation;

    @FXML
    ToggleButton configuration;

    private VBox tableForm;

    private ContextMenuController contextMenuController;

    private TableFormController tableFormController;

    private Set<TableController> tableControllers;

    private RestaurantServices restaurantServices;

    private RestaurantView restaurantView;

    @Inject
    public RestaurantControllerImpl(RestaurantServices restaurantServices, ContextMenuController contextMenuController,
                                    TableFormController tableFormController) {
        this.contextMenuController = contextMenuController;
        this.restaurantServices = restaurantServices;
        this.tableFormController = tableFormController;
    }

    @FXML
    public void onConfigToggle(Event event) {
        if (!configuration.isSelected()) {
            tableControllers.forEach(tableController -> {
                Node view = tableController.getRoot();
                Point2D position = new Point2D(view.getLayoutX(), view.getLayoutY());
                restaurantServices.moveTable(tableController.getView(), position);
                return;
            });
        }
    }

    @FXML
    public void onTablesSelected(Event event) {
        toTablesPane(tableForm);
    }

    @FXML
    public void onVirtualSelected(Event event) {
        toVirtualPane(tableForm);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu();
        initTableForm();
        initRestaurant();
        initTables();
        DragAndDropHandler.setEnableControl(configuration);
    }

    private void initRestaurant() {
        restaurantView = restaurantServices.getActiveRestaurant();
    }

    private void initTables() {
        tableControllers = new HashSet<>();
        restaurantServices.getTables(restaurantView).stream().filter(VISIBLE_TABLE).forEach(this::drawTable);
    }

    private void initContextMenu() {

        addPressAndHold(tables, new RestaurantContextMenuBuilder(), Duration.millis(HOLD_DURATION_MILLIS));
        //addPressAndHold(virtual, contextMenu, Duration.millis(HOLD_DURATION_MILLIS));
    }

    private void initTableForm() {
        tableForm = (VBox) loadViewHidden(TABLE_FORM_VIEW_PATH, tableFormController);
        //tables.getChildren().add(tableForm);
    }

    @Override
    public void showAddTableForm() {
//        Point2D position = getNodePosition(contextMenu);
//        tableFormController.loadTable(null);
//        showNode(tableForm, position);
    }

    @Override
    public void showEditTableForm(Node node) {
//        Point2D position = getNodePosition(contextMenu);
//        TableController tableController = getTableController(root);
//        tableFormController.loadTable(tableController);
//        showNode(tableForm, position);
    }

    @Override
    public void createTable(int tableNumber, int tableCapacity, boolean isVirtual) {
        Point2D position = getNodePosition(tableForm);
        TableType tableType = isVirtual ? VIRTUAL : NORMAL;

        TableView tableView = restaurantServices.addTable(restaurantView, tableType, tableNumber);

        restaurantServices.setTableCapacity(tableView, tableCapacity);
        restaurantServices.moveTable(tableView, position);

        hideNode(tableForm);
        drawTable(tableView);
    }

    @Override
    public void editTable(TableController tableController, Integer tableNumber, Integer tableCapacity, boolean isVirtual) {
        TableView tableView = tableController.getView();
        System.out.println("Edit table " + tableView.getTableNumber());

        restaurantServices.setTableNumber(tableView, tableNumber);
        restaurantServices.setTableType(tableView, isVirtual ? VIRTUAL : NORMAL);
        restaurantServices.setTableCapacity(tableView, tableCapacity);

        hideNode(tableForm);
        Node node = tableController.getRoot();
        if (isVirtual) {
            toVirtualPane(node);
        } else {
            toTablesPane(node);
        }
        tableController.updateNode();
    }

    @Override
    public void deleteTable(Node node) {
        TableView tableView = getTableController(node).getView();
        System.out.println("Delete table " + tableView.getTableNumber());

        restaurantServices.deleteTable(tableView);
    }

    @Override
    public void mergeTables(Node node, int consumedTableNumber) {

    }

    @Override
    public void splitTables(Node node, int producedTableNumber) {

    }

    private void toTablesPane(Node node) {
        //moveNode(virtual, tables, root);
    }

    private void toVirtualPane(Node node) {
        //moveNode(tables, virtual, root);
    }

    private TableController getTableController(Node node) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getRoot().equals(node))
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table root could not be found"));
    }

    private void drawTable(TableView tableView) {
        TableController tableController = new TableControllerImpl(tableView);

        tableControllers.add(tableController);

        loadView(TABLE_VIEW_PATH, tableController);

        addPressAndHold(tableController.getRoot(), new TableContextMenuBuilderDecorator(new RestaurantContextMenuBuilder()), Duration.millis(HOLD_DURATION_MILLIS));

         //pane = tableView.isVirtual() ? virtual : tables;

        tables.getGraphic().getChildren().add(tableController.getRoot());
    }
}
