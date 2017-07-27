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
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
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
import javafx.concurrent.Worker;
import javafx.concurrent.Task;

@Singleton
public class RestaurantControllerImpl implements RestaurantController {

    public static final String RESTAURANT_VIEW_PATH = "/view/fxml/Restaurant.fxml";
    private static final int HOLD_DURATION_MILLIS = 200;
    private static final int INITIAL_GRID_SIZE = 10;
    private static Predicate<TableView> DISPLAYABLE_TABLE = TableView::isDisplayable;

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

    @FXML
    Label liveTime;

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
    public void createTable(String name, Integer number, String note, Integer guestCount, Integer capacity, Dimension2D dimension) {
        TableType tableType = restaurantViewState.getTableType();
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), getActiveTab());
        TableView tableView;

        try {
            tableView = restaurantService.addTable(restaurantView, restaurantService
                    .tableBuilder()
                    .type(tableType)
                    .name(name)
                    .number(number)
                    .note(note)
                    .guestCount(guestCount)
                    .capacity(capacity)
                    .visible(true)
                    .coordinateX((int) position.getX())
                    .coordinateY((int) position.getY())
                    .dimensionX((int) dimension.getWidth())
                    .dimensionY((int) dimension.getHeight()));

            tableForm.hide();

            drawTable(tableView);

            if (tableView.isHosted()) {
                getTableController(tableView.getHost()).updateNode();
            }

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
        TableView previousHost = tableView.getHost();

        try {
            if (tableView.getDisplayedNumber() != number) {
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
            updateHostNodes(tableView, previousHost);
            updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + number);
            initRestaurant();
        } catch (Exception e) {
            initRestaurant();
        }
    }

    private void updateHostNodes(TableView tableView, TableView previousHost) {
        if(tableView.getHost() != null) {
            getTableController(tableView.getHost()).updateNode();
        }
        if(previousHost != null) {
            getTableController(previousHost).updateNode();
        }
    }

    @Override
    public void openTable(Integer number, String name, Integer guestCount, String note) {
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
        editTable(tableController, name, guestCount, note, number, tableView.getCapacity(), tableView.getDimension());
        tableController.openTable(null);
        viewLoader.loadViewIntoScene(this);
    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        TableController hostController = null;

        if (tableView.isHosted()) {
            hostController = getTableController(tableView.getHost());
        }

        try {
            restaurantService.deleteTable(tableView);
            removeNode((Pane) node.getParent(), node);
            tableControllers.remove(tableController);

            if (hostController != null) {
                hostController.updateNode();
            }
        } catch (IllegalTableStateException e) {
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
            TableView consumer = firstSelected.getView();
            List<TableView> consumed = new ArrayList<>();

            selectedTables.stream()
                    .filter(tableController -> !tableController.equals(firstSelected))
                    .forEach(tableController -> consumed.add(tableController.getView()));

            restaurantService.mergeTables(consumer, consumed);

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
                .filter(tableController -> tableController.getView().isNormal() || tableController.getView().isConsumer())
                .map(TableController::getRoot).collect(toList()));
        loiterersTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isLoiterer())
                .map(TableController::getRoot).collect(toList()));
        frequentersTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isFrequenter())
                .map(TableController::getRoot).collect(toList()));
        employeesTab.getChildren().removeAll(tableControllers.stream()
                .filter(tableController -> tableController.getView().isEmployee())
                .map(TableController::getRoot).collect(toList()));
        tableControllers.clear();
        List<TableView> tables = restaurantService.getTables();
        tables.sort(Comparator.comparing(TableView::isConsumed));   // Put consumed tables to the end so the consumer is loaded in advance.
        tables.stream().filter(DISPLAYABLE_TABLE).forEach(this::drawTable);
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

    public TableController getTableController(TableView tableView) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getView().getNumber() == tableView.getNumber())
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table view could not be found"));
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
