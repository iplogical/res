package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.VIRTUAL;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.and;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.not;
import static com.inspirationlogical.receipt.waiter.controller.TableControllerImpl.TABLE_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl.TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.*;
import static com.inspirationlogical.receipt.waiter.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.builder.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.builder.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.builder.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.exception.ViewNotFoundException;
import com.inspirationlogical.receipt.waiter.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.util.Duration;

@Singleton
public class RestaurantControllerImpl implements RestaurantController {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    private static final int HOLD_DURATION_MILLIS = 500;
    private static Predicate<TableView> NORMAL_TABLE = not(TableView::isVirtual);
    private static Predicate<TableView> VISIBLE_TABLE = TableView::isVisible;
    private static Predicate<TableView> NORMAL_VISIBLE_TABLE = and(NORMAL_TABLE, VISIBLE_TABLE);

    @FXML
    AnchorPane root;

    @FXML
    Button consumption;

    @FXML
    Button reservation;

    @FXML
    ToggleButton configuration;

    @FXML
    AnchorPane tablesTab;

    @FXML
    Label tablesLab;

    @FXML
    AnchorPane virtualTab;

    @FXML
    Label virtualLab;

    private Popup tableForm;

    private TableFormController tableFormController;

    private ConfigureTableFormController configureTableFormController;

    private Set<TableController> tableControllers;

    private RestaurantServices restaurantServices;

    private RetailServices retailServices;

    private RestaurantView restaurantView;

    private RestaurantViewState restaurantViewState;

    @Inject
    public RestaurantControllerImpl(RestaurantServices restaurantServices,
                                    RetailServices retailServices,
                                    TableFormController tableFormController,
                                    ConfigureTableFormController configureTableFormController) {
        this.restaurantServices = restaurantServices;
        this.retailServices = retailServices;
        this.tableFormController = tableFormController;
        this.configureTableFormController = configureTableFormController;
        restaurantViewState = new RestaurantViewState();
    }

    @FXML
    public void onConfigToggle(Event event) {
        restaurantViewState.setConfigurationEnabled(configuration.isSelected());
        if (!configuration.isSelected()) {
            tableControllers.forEach(this::saveTablePosition);
        }
    }

    private void saveTablePosition(TableController tableController) {
        Node view = tableController.getRoot();
        Point2D position = new Point2D(view.getLayoutX(), view.getLayoutY());
        restaurantServices.moveTable(tableController.getView(), position);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu(tablesLab);
        initContextMenu(virtualLab);
        initTableForm();
        initRestaurant();
        initTables();
    }

    private void initRestaurant() {
        restaurantView = restaurantServices.getActiveRestaurant();
    }

    private void initTables() {
        tableControllers = new HashSet<>();
        restaurantServices.getTables(restaurantView).stream().filter(VISIBLE_TABLE).forEach(this::drawTable);
    }

    private void initContextMenu(Control control) {
        addPressAndHold(restaurantViewState, control,
                new RestaurantContextMenuBuilderDecorator(this, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
    }

    private void initTableForm() {
        tableForm = new Popup();
        tableForm.getContent().add(loadView(TABLE_FORM_VIEW_PATH, tableFormController));
    }

    @Override
    public void showAddTableForm(Point2D position) {
        tableFormController.loadTable(null);

        tableForm.show(tablesTab, position.getX(), position.getY());
    }

    @Override
    public void showEditTableForm(Control control) {
        TableController tableController = getTableController(control);

        tableFormController.loadTable(tableController);

        Point2D position = calculatePopupPosition(control, tablesTab);

        tableForm.show(tablesTab, position.getX(), position.getY());
    }

    @Override
    public void createTable(int tableNumber, int tableCapacity, boolean isVirtual) {
        TableType tableType = isVirtual ? VIRTUAL : NORMAL;
        Point2D position = calculateTablePosition(tableForm, tablesTab);
        TableView tableView;
        try{
            tableView = restaurantServices.addTable(restaurantView, restaurantServices
                    .tableBuilder()
                    .type(tableType)
                    .number(tableNumber)
                    .name("")
                    .capacity(tableCapacity)
                    .visibility(true)
                    .coordinateX((int) position.getX())
                    .coordinateY((int) position.getY()));
            tableForm.hide();
            drawTable(tableView);
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(tablesLab, Resources.UI.getString("TableAlreadyUsed") + tableNumber);
            initRestaurant();
        } catch (Exception e) {
            initRestaurant();
        }
    }

    @Override
    public void editTable(TableController tableController, Integer tableNumber, Integer tableCapacity, boolean isVirtual) {
        TableView tableView = tableController.getView();

        restaurantServices.setTableNumber(tableView, tableNumber);
        restaurantServices.setTableType(tableView, isVirtual ? VIRTUAL : NORMAL);
        restaurantServices.setTableCapacity(tableView, tableCapacity);

        tableForm.hide();

        Node node = tableController.getRoot();

        addNodeToPane(node, isVirtual);

        tableController.updateNode();
    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();

        restaurantServices.deleteTable(tableView);

        removeNode((Pane) node.getParent(), node);

        tableControllers.remove(tableController);
    }

    @Override
    public void mergeTables(Node node, int consumedTableNumber) {

    }

    @Override
    public void splitTables(Node node, int producedTableNumber) {

    }

    private void addNodeToPane(Node node, boolean isVirtual) {
        if (isVirtual) {
            toVirtualPane(node);
        } else {
            toTablesPane(node);
        }
    }

    private void toTablesPane(Node node) {
        moveNode(virtualTab, tablesTab, node);
    }

    private void toVirtualPane(Node node) {
        moveNode(tablesTab, virtualTab, node);
    }

    private TableController getTableController(Node node) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getRoot().equals(node))
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table root could not be found"));
    }

    private void drawTable(TableView tableView) {
        TableController tableController = new TableControllerImpl(restaurantServices, retailServices, tableView, restaurantViewState, configureTableFormController);

        tableControllers.add(tableController);

        loadView(TABLE_VIEW_PATH, tableController);

        addPressAndHold(tableController.getViewState(), tableController.getRoot(),
                new TableContextMenuBuilderDecorator(this, tableController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));

        addNodeToPane(tableController.getRoot(), tableView.isVirtual());
    }
}
