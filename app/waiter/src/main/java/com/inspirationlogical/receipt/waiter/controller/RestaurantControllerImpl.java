package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.VIRTUAL;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.and;
import static com.inspirationlogical.receipt.corelib.utility.PredicateOperations.not;
import static com.inspirationlogical.receipt.waiter.controller.TableControllerImpl.TABLE_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.controller.TableFormControllerImpl.TABLE_FORM_VIEW_PATH;
import static com.inspirationlogical.receipt.waiter.registry.FXMLLoaderProvider.getInjector;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.calculateTablePosition;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.moveNode;
import static com.inspirationlogical.receipt.waiter.view.NodeUtility.removeNode;
import static com.inspirationlogical.receipt.waiter.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.view.ViewLoader.loadView;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
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
import javafx.scene.image.ImageView;
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

    @FXML
    Pane layoutPane;

    @FXML
    ImageView layoutImage;

    private Popup tableForm;

    private TableFormController tableFormController;

    private TableSettingsFormController tableSettingsFormController;

    private Set<TableController> tableControllers;

    private Set<TableController> selectedTables;

    private RestaurantServices restaurantServices;

    private RetailServices retailServices;

    private RestaurantView restaurantView;

    private RestaurantViewState restaurantViewState;

    @Inject
    public RestaurantControllerImpl(RestaurantServices restaurantServices,
                                    RetailServices retailServices,
                                    TableFormController tableFormController,
                                    TableSettingsFormController tableSettingsFormController) {
        this.restaurantServices = restaurantServices;
        this.retailServices = retailServices;
        this.tableFormController = tableFormController;
        this.tableSettingsFormController = tableSettingsFormController;
        restaurantViewState = new RestaurantViewState();
        restaurantViewState.setFullScreen(true);
    }

    @FXML
    public void onConfigToggle(Event event) {
        restaurantViewState.setConfigurable(configuration.isSelected());
        if (!configuration.isSelected()) {
            tableControllers.forEach(this::saveTablePosition);
            selectedTables.forEach(TableController::deselectTable);
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
        initRestaurant();
        initTables();
        initBackground();
    }

    private void initBackground() {
        layoutImage.fitWidthProperty().bind(layoutPane.widthProperty());
        layoutImage.setPreserveRatio(true);
    }

    private void initRestaurant() {
        restaurantView = restaurantServices.getActiveRestaurant();
    }

    private void initTables() {
        tableControllers = new HashSet<>();
        selectedTables = new LinkedHashSet<>();
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
    public void showCreateTableForm(Point2D position) {
        initTableForm();
        tableFormController.loadTable(null);

        tableForm.show(tablesTab, position.getX(), position.getY());
    }

    @Override
    public void showEditTableForm(Control control) {
        initTableForm();
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

        if (tableCapacity < tableView.getGuestCount()) {
            ErrorMessage.showErrorMessage(tablesLab, Resources.UI.getString("CapacityTooLow"));
            return;
        }

        try{
            restaurantServices.setTableNumber(tableView, tableNumber);
            restaurantServices.setTableType(tableView, isVirtual ? VIRTUAL : NORMAL);
            restaurantServices.setTableCapacity(tableView, tableCapacity);

            tableForm.hide();

            Node node = tableController.getRoot();
            addNodeToPane(node, isVirtual);
            tableController.updateNode();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(tablesLab, Resources.UI.getString("TableAlreadyUsed") + tableNumber);
            initRestaurant();
        } catch (Exception e) {
            initRestaurant();
        }
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
    public void mergeTables() {
        if (selectedTables.size() > 1) {
            TableController firstSelected = selectedTables.iterator().next();
            TableView aggregate = firstSelected.getView();
            List<TableView> consumed = new ArrayList<>();
            selectedTables.remove(firstSelected);
            selectedTables.iterator().forEachRemaining(tableController -> {
                consumed.add(tableController.getView());
            });

            restaurantServices.mergeTables(aggregate, consumed);
            selectedTables.stream().forEach(tableController -> {
                tablesTab.getChildren().remove(tableController.getRoot());
                tableControllers.remove(tableController);
            });
            selectedTables.clear();
            firstSelected.deselectTable();
        } else {
            ErrorMessage.showErrorMessage(tablesLab, Resources.UI.getString("InsufficientSelection"));
        }
    }

    @Override
    public void selectTable(TableController tableController, boolean selected) {
        // todo virtual or not open tables can't be selected
        if (selected) {
            selectedTables.add(tableController);
        } else {
            selectedTables.remove(tableController);
        }
    }

    @Override
    public RestaurantViewState getViewState() {
        return restaurantViewState;
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
        TableController tableController = getInjector().getInstance(TableController.class);
        tableController.setView(tableView);

        tableControllers.add(tableController);

        loadView(TABLE_VIEW_PATH, tableController);

        addPressAndHold(tableController.getViewState(), tableController.getRoot(),
                new TableContextMenuBuilderDecorator(this, tableController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));

        addNodeToPane(tableController.getRoot(), tableView.isVirtual());
    }

    @Override
    public Node getRootNode() {
        return root;
    }
}
