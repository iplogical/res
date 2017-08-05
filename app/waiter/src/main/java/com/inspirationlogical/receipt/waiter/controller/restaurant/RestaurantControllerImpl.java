package com.inspirationlogical.receipt.waiter.controller.restaurant;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculateTablePosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.moveNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.removeNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.service.CommonService;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.contextmenu.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.dailysummary.DailySummaryController;
import com.inspirationlogical.receipt.waiter.controller.reservation.ReservationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.exception.ViewNotFoundException;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import com.inspirationlogical.receipt.waiter.utility.ConfirmMessage;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
import javafx.concurrent.Worker;
import javafx.concurrent.Task;

@Singleton
public class RestaurantControllerImpl implements RestaurantController {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    static final int HOLD_DURATION_MILLIS = 200;
    static final int INITIAL_GRID_SIZE = 10;
   static Predicate<TableView> DISPLAYABLE_TABLE = TableView::isDisplayable;

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
    private Label openTableNumber;

    @FXML
    private Label totalTableNumber;

    @FXML
    private Label totalGuests;

    @FXML
    private Label totalCapacity;

    @FXML
    private Label openConsumption;

    @FXML
    private Label paidConsumption;

    @FXML
    private Label totalIncome;

    @FXML
    Label liveTime;

    @Inject
    ViewLoader viewLoader;

    Popup tableForm;

    TableFormController tableFormController;

    Set<TableController> tableControllers;

    private Set<TableController> selectedTables;

    RestaurantService restaurantService;

    RestaurantView restaurantView;

    RestaurantViewState restaurantViewState;

    TableConfigurationController tableConfigurationController;

    @Inject
    public RestaurantControllerImpl(RestaurantService restaurantService,
                                    TableFormController tableFormController,
                                    TableConfigurationController tableConfigurationController) {
        this.restaurantService = restaurantService;
        this.tableFormController = tableFormController;
        this.tableConfigurationController = tableConfigurationController;
        tableControllers = new HashSet<>();
        selectedTables = new LinkedHashSet<>();
        restaurantViewState = new RestaurantViewState(selectedTables);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new RestaurantControllerInitializer(this).initialize();
        initLiveTime(liveTime);
        updateRestaurantSummary();
    }

    @Override
    public int getFirstUnusedTableNumber() {
        return restaurantService.getFirstUnusedNumber();
    }

    @Override
    public void showCreateTableForm(Point2D position) {
        tableFormController.loadTable(null, restaurantViewState.getTableType());
        showPopup(tableForm, tableFormController, getActiveTab(), position);
    }

    @Override
    public void showEditTableForm(Control control) {
        TableController tableController = getTableController(control);
        tableFormController.loadTable(tableController, restaurantViewState.getTableType());
        Point2D position = calculatePopupPosition(control, getActiveTab());
        showPopup(tableForm, tableFormController, getActiveTab(), position);
    }

    @Override
    public void createTable(TableParams tableParams) {
        tableConfigurationController.createTable(tableParams);
//        try {
//            TableView tableView = restaurantService.addTable(restaurantView, buildTable(tableParams));
//            tableForm.hide();
//            drawTable(tableView);
//            if (tableView.isHosted()) {
//                getTableController(tableView.getHost()).updateTable();
//            }
//            updateRestaurantSummary();
//        } catch (IllegalTableStateException e) {
//            ErrorMessage.showErrorMessage(getActiveTab(),
//                    Resources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
////            initRestaurant();
//        }
    }
//
//    private Table.TableBuilder buildTable(TableParams params) {
//        TableType tableType = restaurantViewState.getTableType();
//        Point2D position = calculateTablePosition(tableFormController.getRootNode(), getActiveTab());
//        return restaurantService
//                .tableBuilder()
//                .type(tableType)
//                .name(params.getName())
//                .number(params.getNumber())
//                .note(params.getNote())
//                .guestCount(params.getGuestCount())
//                .capacity(params.getCapacity())
//                .visible(true)
//                .coordinateX((int) position.getX())
//                .coordinateY((int) position.getY())
//                .dimensionX((int) params.getDimension().getWidth())
//                .dimensionY((int) params.getDimension().getHeight());
//    }

    @Override
    public void editTable(TableController tableController, TableParams tableParams) {
        tableConfigurationController.editTable(tableController, tableParams);
//        TableView tableView = tableController.getView();
//        TableView previousHost = tableView.getHost();
//
//        try {
//            setTableParams(tableView, tableParams);
//            addNodeToPane(tableController.getRoot(), restaurantViewState.getTableType());
//            tableController.updateTable();
//            updateHostNodes(tableView, previousHost);
//            updateRestaurantSummary();
//        } catch (IllegalTableStateException e) {
//            ErrorMessage.showErrorMessage(getActiveTab(),
//                    Resources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
////            initRestaurant();
//        }
    }

//    private void setTableParams(TableView tableView, TableParams tableParams) {
//        if (notOwnNumberIsDisplayed(tableView, tableParams)) {
//            restaurantService.setTableNumber(tableView, tableParams.getNumber(), restaurantView);
//        }
//        restaurantService.setTableParams(tableView, tableParams);
//        tableForm.hide();
//    }

//    private boolean notOwnNumberIsDisplayed(TableView tableView, TableParams tableParams) {
//        return tableView.getDisplayedNumber() != tableParams.getNumber();
//    }
//
//    private void updateHostNodes(TableView tableView, TableView previousHost) {
//        if(tableView.getHost() != null) {
//            getTableController(tableView.getHost()).updateTable();
//        }
//        if(previousHost != null) {
//            getTableController(previousHost).updateTable();
//        }
//    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        try {
            restaurantService.deleteTable(tableView);
            removeNode((Pane) node.getParent(), node);
            tableControllers.remove(tableController);
            updateHostTable(tableView);
        } catch (IllegalTableStateException e) {
            showDeleteTableErrorMessage(tableView);
//            initRestaurant();
        }
    }

    private void updateHostTable(TableView tableView) {
        if (tableView.isHosted()) {
            TableController hostController = getTableController(tableView.getHost());
            hostController.updateTable();
        }
    }

    private void showDeleteTableErrorMessage(TableView tableView) {
        String errorMessage = EMPTY;
        if (tableView.isOpen()) {
            errorMessage = Resources.WAITER.getString("TableIsOpen");
        }
        if (tableView.isConsumer()) {
            errorMessage = Resources.WAITER.getString("TableIsConsumer");
        }
        if (tableView.isHost()) {
            errorMessage = Resources.WAITER.getString("TableIsHost");
        }
        ErrorMessage.showErrorMessage(getActiveTab(), errorMessage + tableView.getNumber());
    }

    @Override
    public void rotateTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        restaurantService.rotateTable(tableView);
        tableController.updateTable();
    }

    @Override
    public void moveTable(TableController tableController) {
        Node view = tableController.getRoot();
        Point2D position = new Point2D(view.getLayoutX(), view.getLayoutY());
        restaurantService.setTablePosition(tableController.getView(), position);
        tableController.updateTable();
    }

    @Override
    public void moveTable(TableView tableView, Point2D position) {
        restaurantService.setTablePosition(tableView, position);
    }

    @Override
    public void mergeTables() {
        if(selectedTables.size() < 2) {
            ErrorMessage.showErrorMessage(tablesControl, Resources.WAITER.getString("InsufficientSelection"));
            return;
        }
        TableController consumerController = selectedTables.iterator().next();
        TableView consumer = consumerController.getView();
        try {
            restaurantService.mergeTables(consumer, getConsumedTables(consumerController));
            updateConsumer(consumerController);
            updateConsumed();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(tablesControl, Resources.WAITER.getString("ConsumerSelectedForMerge"));
        }
    }

    private List<TableView> getConsumedTables(TableController firstSelected) {
        List<TableView> consumed = new ArrayList<>();
        selectedTables.stream()
                .filter(tableController -> !tableController.equals(firstSelected))
                .forEach(tableController -> consumed.add(tableController.getView()));
        return consumed;
    }

    public void updateConsumer(TableController consumerController) {
        consumerController.consumeTables();
        consumerController.updateTable();
        consumerController.deselectTable();
        selectedTables.remove(consumerController);
    }

    public void updateConsumed() {
        selectedTables.forEach(tableController -> {
            tableControllers.remove(tableController);
            tablesTab.getChildren().remove(tableController.getRoot());
        });
        selectedTables.clear();
    }

    @Override
    public void splitTables(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        List<TableView> tables = restaurantService.splitTables(tableView);
        tables.forEach(this::drawTable);
        tableController.releaseConsumedTables();
        tableController.updateTable();
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
    public void openTableOfReservation(Integer number, String name, Integer guestCount, String note) {
        List<TableController> filteredControllers = tableControllers.stream()
                .filter(controller -> controller.getView().getNumber() == number)
                .limit(1)
                .collect(toList());
        if(filteredControllers.isEmpty()) {
            viewLoader.loadViewIntoScene(this);
            ErrorMessage.showErrorMessage(getActiveTab(), Resources.WAITER.getString("TableDoesNotExist") + number);
            return;
        }
        TableController tableController = filteredControllers.get(0);
        TableView tableView = tableController.getView();
        if(tableView.isOpen()) {
            viewLoader.loadViewIntoScene(this);
            ErrorMessage.showErrorMessage(getActiveTab(), Resources.WAITER.getString("TableIsOpenReservation") + tableView.getNumber());
            return;
        }
        TableParams tableParams = buildTableParams(number, name, guestCount, note, tableView);
        editTable(tableController, tableParams);
        tableController.openTable(null);
        viewLoader.loadViewIntoScene(this);
    }

    private TableParams buildTableParams(Integer number, String name, Integer guestCount, String note, TableView tableView) {
        return TableParams.builder()
                .name(name)
                .number(number)
                .note(note)
                .guestCount(guestCount)
                .capacity(tableView.getCapacity())
                .dimension(tableView.getDimension())
                .build();
    }

    @Override
    public void updateRestaurant() {
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
        ReservationController reservationController = WaiterRegistry.getInstance(ReservationController.class);
        reservationController.setRestaurantView(restaurantView);
        viewLoader.loadViewIntoScene(reservationController);
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
        switch (restaurantViewState.getTableType()) {
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
    public TableController getTableController(Node node) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getRoot().equals(node))
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table root could not be found"));
    }

    @Override
    public TableController getTableController(TableView tableView) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getView().getNumber() == tableView.getNumber())
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table view could not be found"));
    }

    @Override
    public void drawTable(TableView tableView) {
        TableController tableController = WaiterRegistry.getInstance(TableController.class);
        tableController.setView(tableView);
        tableControllers.add(tableController);
        viewLoader.loadView(tableController);
        addPressAndHold(tableController.getViewState(), tableController.getRoot(),
                new TableContextMenuBuilderDecorator(this, tableConfigurationController, tableController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
        addNodeToPane(tableController.getRoot(), tableView.getType());
    }

    @Override
    public String getViewPath() {
        return RESTAURANT_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return rootRestaurant;
    }

    @Override
    public void updateRestaurantSummary() {
        final Task task = new RestaurantSummaryController(tableControllers, restaurantView).getTask();

        task.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    ObservableList<String> values = (ObservableList<String>)task.valueProperty().getValue();
                    totalTableNumber.setText(values.get(0));
                    openTableNumber.setText(values.get(1));
                    totalGuests.setText(values.get(2));
                    totalCapacity.setText(values.get(3));
                    openConsumption.setText(values.get(4));
                    paidConsumption.setText(values.get(5));
                    totalIncome.setText(String.valueOf(Integer.valueOf(openConsumption.getText()) + Integer.valueOf(paidConsumption.getText())));
                }
            }
        });
        new Thread(task).start();
    }
}
