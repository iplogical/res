package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.dailysummary.DailySummaryController;
import com.inspirationlogical.receipt.waiter.controller.reservation.ReservationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.ConfirmMessage;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.ResourceBundle;
import java.util.Set;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.moveNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.HOLD_DURATION_MILLIS;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;

@Singleton
public class RestaurantControllerImpl implements RestaurantController {

    final private static Logger logger = LoggerFactory.getLogger(RestaurantControllerImpl.class);

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    static final int INITIAL_GRID_SIZE = 10;

    @FXML
    private AnchorPane rootRestaurant;

    @FXML
    private Button dailySummary;

    @FXML
    private Button reservation;

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
    Label liveTime;

    @Inject
    ViewLoader viewLoader;

    Popup tableForm;

    private Set<TableController> selectedTables;

    RestaurantService restaurantService;

    RestaurantView restaurantView;

    RestaurantViewState restaurantViewState;

    TableConfigurationController tableConfigurationController;

    @Inject
    public RestaurantControllerImpl(RestaurantService restaurantService,
                                    TableConfigurationController tableConfigurationController) {
        this.restaurantService = restaurantService;
        this.tableConfigurationController = tableConfigurationController;
        selectedTables = new LinkedHashSet<>();
        restaurantViewState = new RestaurantViewState(selectedTables);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initContextMenu(tablesControl);
        initContextMenu(loiterersControl);
        initContextMenu(frequentersControl);
        initContextMenu(employeesControl);
        initControls();
        initRestaurant();
        tableConfigurationController.initialize();
        initLiveTime(liveTime);
    }

    private void initContextMenu(Label control) {
        addPressAndHold(restaurantViewState, control,
                new RestaurantContextMenuBuilderDecorator(tableConfigurationController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
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
    }

    @Override
    public RestaurantViewState getViewState() {
        return restaurantViewState;
    }

    @FXML
    public void onConfigurationToggle(Event event) {
        logger.info("The configuration mode was toggled: is config mode: " + configuration.isSelected());
        if (!configuration.isSelected()) {
            tableConfigurationController.clearSelections();
        }
    }

    @FXML
    public void onDailyClosure(Event event) {
        logger.info("The Daily Closure was pressed in the RestaurantView.");
        ConfirmMessage.showConfirmDialog(WaiterResources.WAITER.getString("Restaurant.DailyClosureConfirm"), () -> restaurantService.closeDay());
    }

    @FXML
    public void onDailySummary(Event event) {
        DailySummaryController dailySummaryController = WaiterRegistry.getInstance(DailySummaryController.class);
        dailySummaryController.setRestaurantView(restaurantView);
        dailySummaryController.setOpenConsumption(getOpenConsumption());
        viewLoader.loadViewIntoScene(dailySummaryController);
        dailySummaryController.updatePriceFields();
        logger.info("Entering the Daily Summary.");
    }

    private String getOpenConsumption() {
        return String.valueOf(tableConfigurationController.getTableControllers().stream()
                .filter(tableController -> tableController.getView().isOpen())
                .mapToInt(tableController -> tableController.getView().getTotalPrice()).sum());
    }

    @FXML
    public void onReservationClicked(Event event) {
        ReservationController reservationController = WaiterRegistry.getInstance(ReservationController.class);
        reservationController.setRestaurantView(restaurantView);
        viewLoader.loadViewIntoScene(reservationController);
        logger.info("Entering the Reservations.");
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

    @Override
    public void addNodeToPane(Node node, TableType tableType) {
        switch (tableType) {
            case NORMAL:
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

    @Override
    public Pane getActiveTab() {
        return getTableTabByTableType(restaurantViewState.getTableType());
    }

    @Override
    public Pane getTab(TableType tableType) {
        return getTableTabByTableType(tableType);
    }

    private Pane getTableTabByTableType(TableType tableType) {
        switch (tableType) {
            case NORMAL:
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

    @Override
    public String getViewPath() {
        return RESTAURANT_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return rootRestaurant;
    }

}
