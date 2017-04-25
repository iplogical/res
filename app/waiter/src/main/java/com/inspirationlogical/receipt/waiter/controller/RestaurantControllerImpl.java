package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculateTablePosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.moveNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.removeNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.builder.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.builder.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.builder.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.exception.ViewNotFoundException;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.ConfirmMessage;
import com.inspirationlogical.receipt.waiter.viewstate.RestaurantViewState;

import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.util.Duration;

@Singleton
public class RestaurantControllerImpl implements RestaurantController {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    private static final int HOLD_DURATION_MILLIS = 200;
    private static final int INITIAL_GRID_SIZE = 10;
    private static Predicate<TableView> VISIBLE_TABLE = TableView::isVisible;

    @FXML
    AnchorPane root;

    @FXML
    Button dailySummary;

    @FXML
    Button reservation;

    @FXML
    ToggleButton configuration;

    @FXML
    ToggleButton motion;

    @FXML
    CheckBox snapToGrid;

    @FXML
    Slider setGridSize;

    @FXML
    Label getGridSize;

    @FXML
    AnchorPane tablesTab;

    @FXML
    Label tablesControl;

    @FXML
    AnchorPane loiterersTab;

    @FXML
    Label loiterersControl;

    @FXML
    AnchorPane frequentersTab;

    @FXML
    Label frequentersControl;

    @FXML
    AnchorPane employeesTab;

    @FXML
    Label employeesControl;

    @FXML
    Label openTableNumber;

    @FXML
    Label totalTableNumber;

    @FXML
    Label totalGuests;

    @FXML
    Label totalCapacity;

    @FXML
    Label openConsumption;

    @FXML
    Label paidConsumption;

    @FXML
    Label totalIncome;

    @Inject
    private ViewLoader viewLoader;

    private Popup tableForm;

    private TableFormController tableFormController;

    private Set<TableController> tableControllers;

    private Set<TableController> selectedTables;

    private RestaurantService restaurantService;

    private RestaurantView restaurantView;

    private RestaurantViewState restaurantViewState;

    @Inject
    public RestaurantControllerImpl(RestaurantService restaurantService,
                                    RetailService retailService,
                                    CommonService commonService,
                                    TableFormController tableFormController) {
        this.restaurantService = restaurantService;
        this.tableFormController = tableFormController;
        tableControllers = new HashSet<>();
        selectedTables = new LinkedHashSet<>();
        restaurantViewState = new RestaurantViewState(selectedTables);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu(tablesControl);
        initContextMenu(loiterersControl);
        initContextMenu(frequentersControl);
        initContextMenu(employeesControl);
        initTableForm();
        initControls();
        initRestaurant();
        updateRestaurantSummary();
    }

    @Override
    public int getFirstUnusedTableNumber() {
        return restaurantService.getFirstUnusedNumber();
    }

    @Override
    public void showCreateTableForm(Point2D position) {
        tableFormController.loadTable(null);

        showPopup(tableForm, tableFormController, getActiveTab(), position);
    }

    @Override
    public void showEditTableForm(Control control) {
        TableController tableController = getTableController(control);

        tableFormController.loadTable(tableController);

        Point2D position = calculatePopupPosition(control, getActiveTab());

        showPopup(tableForm, tableFormController, getActiveTab(), position);
    }

    @Override
    public void createTable(Integer number, Integer capacity, Dimension2D dimmension) {
        TableType tableType = restaurantViewState.getTableType();
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), getActiveTab());
        TableView tableView;

        try {
            tableView = restaurantService.addTable(restaurantView, restaurantService
                    .tableBuilder()
                    .type(tableType)
                    .number(number)
                    .capacity(capacity)
                    .visible(true)
                    .coordinateX((int) position.getX())
                    .coordinateY((int) position.getY())
                    .dimensionX((int) dimmension.getWidth())
                    .dimensionY((int) dimmension.getHeight()));

            tableForm.hide();

            drawTable(tableView);
            updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + number);
            initRestaurant();
        } catch (Exception e) {
            initRestaurant();
        }
    }

    @Override
    public void editTable(TableController tableController, String name, Integer guestCount, String note,
                          Integer number, Integer capacity, Dimension2D dimension) {
        TableView tableView = tableController.getView();

        try {
            if (tableView.getNumber() != number) {
                restaurantService.setTableNumber(tableView, number, restaurantView);
            }
            restaurantService.setTableType(tableView, tableView.getType());
            restaurantService.setTableCapacity(tableView, capacity);
            restaurantService.setTableName(tableView, name);
            restaurantService.setGuestCount(tableView, guestCount);
            restaurantService.addTableNote(tableView, note);
            restaurantService.setTableDimension(tableView, dimension);

            tableForm.hide();

            Node node = tableController.getRoot();
            addNodeToPane(node, restaurantViewState.getTableType());
            tableController.updateNode();
            updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + number);
            initRestaurant();
        } catch (Exception e) {
            initRestaurant();
        }
    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();

        try {
            restaurantService.deleteTable(tableView);
            removeNode((Pane) node.getParent(), node);
            tableControllers.remove(tableController);
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(getActiveTab(),
                    Resources.WAITER.getString("TableIsOpen") + tableView.getNumber());
            initRestaurant();
        }
    }

    @Override
    public void rotateTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();

        restaurantService.rotateTable(tableView);

        tableController.updateNode();
    }

    @Override
    public void moveTable(TableController tableController) {
        Node view = tableController.getRoot();
        Point2D position = new Point2D(view.getLayoutX(), view.getLayoutY());

        restaurantService.setTablePosition(tableController.getView(), position);

        tableController.updateNode();
    }

    @Override
    public void moveTable(TableView tableView, Point2D position) {

        restaurantService.setTablePosition(tableView, position);
    }

    @Override
    public void mergeTables() {
        if (selectedTables.size() > 1) {
            TableController firstSelected = selectedTables.iterator().next();
            TableView aggregate = firstSelected.getView();
            List<TableView> consumed = new ArrayList<>();

            selectedTables.stream()
                    .filter(tableController -> !tableController.equals(firstSelected))
                    .forEach(tableController -> {
                consumed.add(tableController.getView());
            });

            restaurantService.mergeTables(aggregate, consumed);

            firstSelected.consumeTables();
            firstSelected.updateNode();

            firstSelected.deselectTable();
            selectedTables.remove(firstSelected);

            selectedTables.forEach(tableController -> {
                tableControllers.remove(tableController);
                tablesTab.getChildren().remove(tableController.getRoot());
            });

            selectedTables.clear();
        } else {
            ErrorMessage.showErrorMessage(tablesControl, Resources.WAITER.getString("InsufficientSelection"));
        }
    }

    @Override
    public void splitTables(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();

        List<TableView> tables = restaurantService.splitTables(tableView);

        tables.forEach(this::drawTable);

        tableController.releaseConsumed();
        tableController.updateNode();
    }

    @Override
    public void selectTable(TableController tableController, boolean selected) {
        if (selected) {
            selectedTables.add(tableController);
        } else {
            selectedTables.remove(tableController);
        }
    }

    @Override
    public void updateRestaurant() {
        tableControllers.forEach(TableController::updateNode);
        updateRestaurantSummary();
    }

    @Override
    public RestaurantViewState getViewState() {
        return restaurantViewState;
    }

    @FXML
    public void onConfigurationToggle(Event event) {
        if (!configuration.isSelected()) {
            selectedTables.forEach(TableController::deselectTable);
        }
    }

    @FXML
    public void onDailyClosure(Event event) {
        ConfirmMessage.showConfirmDialog(Resources.WAITER.getString("Restaurant.DailyClosureConfirm"), () -> restaurantService.closeDay());
        updateRestaurantSummary();
    }

    @FXML
    public void onDailySummary(Event event) {
        DailySummaryController dailySummaryController = WaiterRegistry.getInstance(DailySummaryController.class);
        dailySummaryController.setRestaurantView(restaurantView);
        dailySummaryController.setOpenConsumption(openConsumption.getText());
        viewLoader.loadViewIntoScene(dailySummaryController);
        dailySummaryController.updatePriceFields();
    }

    @FXML
    public void onReservationClicked(Event event) {
        Point2D position = calculatePopupPosition(reservation, root);
        new ReservationFormController(viewLoader).show(root,position);
    }

    @FXML
    public void onTablesSelected(Event event) {
        restaurantViewState.setTableType(TableType.NORMAL);
    }

    @FXML
    public void onLoiterersSelected(Event event) {
        restaurantViewState.setTableType(TableType.LOITERER);
    }

    @FXML
    public void onFrequentersSelected(Event event) {
        restaurantViewState.setTableType(TableType.FREQUENTER);
    }

    @FXML
    public void onEmployeesSelected(Event event) {
        restaurantViewState.setTableType(TableType.EMPLOYEE);
    }

    private void initControls() {
        restaurantViewState.setConfigurable(configuration.selectedProperty());
        restaurantViewState.getMotionViewState().setMovableProperty(motion.selectedProperty());
        restaurantViewState.getMotionViewState().setSnapToGridProperty(snapToGrid.selectedProperty());
        restaurantViewState.getMotionViewState().setGridSizeProperty(setGridSize.valueProperty());
        snapToGrid.disableProperty().bind(motion.selectedProperty().not());
        setGridSize.disableProperty().bind(motion.selectedProperty().not());
        getGridSize.textProperty().bind(Bindings.format("%.0f", setGridSize.valueProperty()));
        setGridSize.setValue(INITIAL_GRID_SIZE);
    }

    private void initRestaurant() {
        restaurantView = restaurantService.getActiveRestaurant();
        initTables();
    }

    private void initTables() {
        tablesTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isNormal() || tableController.getView().isAggregate())
                .map(TableController::getRoot).collect(Collectors.toList()));
        loiterersTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isLoiterer())
                .map(TableController::getRoot).collect(Collectors.toList()));
        frequentersTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isFrequenter())
                .map(TableController::getRoot).collect(Collectors.toList()));
        employeesTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isEmployee())
                .map(TableController::getRoot).collect(Collectors.toList()));
        tableControllers.clear();
        restaurantService.getTables(restaurantView).stream().filter(VISIBLE_TABLE).forEach(this::drawTable);
    }

    private void initContextMenu(Control control) {
        addPressAndHold(restaurantViewState, control,
                new RestaurantContextMenuBuilderDecorator(this, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
    }

    private void initTableForm() {
        tableForm = new Popup();
        tableForm.getContent().add(viewLoader.loadView(tableFormController));
    }

    private void addNodeToPane(Node node, TableType tableType) {
        switch (tableType) {
            case NORMAL:
            case AGGREGATE:
                moveNode(node, tablesTab);
                break;
            case LOITERER:
                moveNode(node, loiterersTab);
                break;
            case FREQUENTER:
                moveNode(node, frequentersTab);
                break;
            case EMPLOYEE:
                moveNode(node, employeesTab);
                break;
        }
    }

    private Pane getActiveTab() {
        switch (restaurantViewState.getTableType()) {
            case NORMAL:
            case AGGREGATE:
                return tablesTab;
            case LOITERER:
                return loiterersTab;
            case FREQUENTER:
                return frequentersTab;
            case EMPLOYEE:
                return employeesTab;
            default:
                return tablesTab;
        }
    }

    private TableController getTableController(Node node) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getRoot().equals(node))
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table root could not be found"));
    }

    private void drawTable(TableView tableView) {
        TableController tableController = WaiterRegistry.getInstance(TableController.class);
        tableController.setView(tableView);

        tableControllers.add(tableController);

        viewLoader.loadView(tableController);

        addPressAndHold(tableController.getViewState(), tableController.getRoot(),
                new TableContextMenuBuilderDecorator(this, tableController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));

        addNodeToPane(tableController.getRoot(), tableView.getType());
    }

    @Override
    public String getViewPath() {
        return RESTAURANT_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    private void updateRestaurantSummary() {
        // TODO: Icons for the ImageViews.
        totalTableNumber.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isLoiterer())
                        .count()));
        openTableNumber.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isLoiterer())
                        .filter(tableController -> tableController.getView().isOpen())
                        .count()));
        totalGuests.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isLoiterer())
                        .mapToInt(controller -> controller.getView().getGuestCount()).sum()));
        totalCapacity.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isLoiterer())
                        .mapToInt(controller -> controller.getView().getCapacity()).sum()));
        openConsumption.setText(String.valueOf(tableControllers.stream()
                .filter(tableController -> tableController.getView().isOpen())
                .mapToInt(tableController -> tableController.getView().getTotalPrice()).sum()));
        paidConsumption.setText(String.valueOf(restaurantView.getConsumptionOfTheDay(receipt -> !receipt.getPaymentMethod().equals(PaymentMethod.COUPON))));
        totalIncome.setText(String.valueOf(Integer.valueOf(openConsumption.getText()) + Integer.valueOf(paidConsumption.getText())));
    }
}
