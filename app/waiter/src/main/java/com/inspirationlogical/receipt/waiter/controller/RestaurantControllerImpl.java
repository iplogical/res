package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculateTablePosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.moveNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.removeNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.corelib.model.enums.Orientation.HORIZONTAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.NORMAL;
import static com.inspirationlogical.receipt.corelib.model.enums.TableType.VIRTUAL;

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
    Label tablesLab;

    @FXML
    AnchorPane virtualTab;

    @FXML
    Label virtualLab;

    @FXML
    Label openTableNumber;

    @FXML
    Label totalTableNumber;

    @FXML
    Label totalGuests;

    @FXML
    Label totalCapacity;

    @FXML
    Label currentConsumption;

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
        restaurantViewState = new RestaurantViewState();
        tableControllers = new HashSet<>();
        selectedTables = new LinkedHashSet<>();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu(tablesLab);
        initContextMenu(virtualLab);
        initTableForm();
        initControls();
        initRestaurant();
        updateRestaurantSummary();
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
    public void createTable(int tableNumber, int tableCapacity) {
        TableType tableType = restaurantViewState.isVirtual() ? VIRTUAL : NORMAL;
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), getActiveTab());
        TableView tableView;

        try {
            tableView = restaurantService.addTable(restaurantView, restaurantService
                    .tableBuilder()
                    .type(tableType)
                    .number(tableNumber)
                    .capacity(tableCapacity)
                    .visible(true)
                    .coordinateX((int) position.getX())
                    .coordinateY((int) position.getY())
                    .orientation(HORIZONTAL));

            tableForm.hide();

            drawTable(tableView);
            updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + tableNumber);
            initRestaurant();
        } catch (Exception e) {
            initRestaurant();
        }
    }

    @Override
    public void editTable(TableController tableController, Integer tableNumber, Integer tableCapacity) {
        TableView tableView = tableController.getView();

        try {
            if (tableView.getTableNumber() != tableNumber) {
                restaurantService.setTableNumber(tableView, tableNumber, restaurantView);
            }
            restaurantService.setTableType(tableView, restaurantViewState.isVirtual() ? VIRTUAL : NORMAL);
            restaurantService.setTableCapacity(tableView, tableCapacity);

            tableForm.hide();

            Node node = tableController.getRoot();
            addNodeToPane(node, restaurantViewState.isVirtual());
            tableController.updateNode();
            updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + tableNumber);
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
                    Resources.WAITER.getString("TableIsOpen") + tableView.getTableNumber());
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

        restaurantService.moveTable(tableController.getView(), position);

        tableController.updateNode();
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

            restaurantService.mergeTables(aggregate, consumed);
            selectedTables.forEach(tableController -> {
                tablesTab.getChildren().remove(tableController.getRoot());
                tableControllers.remove(tableController);
            });
            selectedTables.clear();
            firstSelected.deselectTable();
            firstSelected.updateNode();
        } else {
            ErrorMessage.showErrorMessage(tablesLab, Resources.WAITER.getString("InsufficientSelection"));
        }
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
    }

    @FXML
    public void onDailySummary(Event event) {
        DailySummaryController dailySummaryController = WaiterRegistry.getInstance(DailySummaryController.class);
        viewLoader.loadViewIntoScene(dailySummaryController);
    }

    @FXML
    public void onReservationClicked(Event event) {
        Point2D position = calculatePopupPosition(reservation, root);
        new ReservationFormController(viewLoader).show(root,position);
    }

    @FXML
    public void onTablesSelected(Event event) {
        restaurantViewState.setVirtual(false);
    }

    @FXML
    public void onVirtualSelected(Event event) {
        restaurantViewState.setVirtual(true);
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
                .filter(tableController -> !tableController.getView().isVirtual())
                .map(TableController::getRoot).collect(Collectors.toList()));
        virtualTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isVirtual())
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

    private void addNodeToPane(Node node, boolean isVirtual) {
        if (isVirtual) {
            toVirtualPane(node);
        } else {
            toTablesPane(node);
        }
    }

    private Pane getActiveTab() {
        return restaurantViewState.isVirtual() ? virtualTab : tablesTab;
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
        TableController tableController = WaiterRegistry.getInstance(TableController.class);
        tableController.setView(tableView);

        tableControllers.add(tableController);

        viewLoader.loadView(tableController);

        addPressAndHold(tableController.getViewState(), tableController.getRoot(),
                new TableContextMenuBuilderDecorator(this, tableController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));

        addNodeToPane(tableController.getRoot(), tableView.isVirtual());
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
                        .filter(tableController -> !tableController.getView().isVirtual())
                        .count()));
        openTableNumber.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isVirtual())
                        .filter(tableController -> tableController.getView().isOpen())
                        .count()));
        totalGuests.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isVirtual())
                        .mapToInt(controller -> controller.getView().getGuestCount()).sum()));
        totalCapacity.setText(String.valueOf(tableControllers.stream()
                        .filter(tableController -> !tableController.getView().isVirtual())
                        .mapToInt(controller -> controller.getView().getTableCapacity()).sum()));
        currentConsumption.setText(String.valueOf(tableControllers.stream()
                .filter(tableController -> tableController.getView().isOpen())
                .mapToInt(tableController -> tableController.getView().getTotalPrice()).sum()));
        paidConsumption.setText(String.valueOf(restaurantView.getPaidConsumptionOfTheDay()));
        totalIncome.setText(String.valueOf(Integer.valueOf(currentConsumption.getText()) + Integer.valueOf(paidConsumption.getText())));
    }
}
