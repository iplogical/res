package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.registry.WaiterRegistry;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Setter;

import java.util.*;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.*;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantControllerImpl.HOLD_DURATION_MILLIS;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Singleton
public class TableConfigurationControllerImpl implements TableConfigurationController {

    @Setter private ViewLoader viewLoader;
    @Setter private RestaurantController restaurantController;
    @Setter private RestaurantViewState restaurantViewState;

    private TableFormController tableFormController;
    private RestaurantService restaurantService;

    Set<TableController> tableControllers;
    private Set<TableController> selectedTables;

    private RestaurantView restaurantView;
    Popup tableForm;

    @Inject
    public TableConfigurationControllerImpl(TableFormController tableFormController,
                                            RestaurantService restaurantService) {
        this.tableFormController = tableFormController;
        this.restaurantService = restaurantService;
        restaurantView = restaurantService.getActiveRestaurant();
        tableControllers = new HashSet<>();
        selectedTables = new LinkedHashSet<>();
    }

    @Override
    public void initialize() {
        tableForm = new Popup();
        tableForm.getContent().add(viewLoader.loadView(tableFormController));
    }

    @Override
    public void showCreateTableForm(Point2D position) {
        tableFormController.loadTable(null, restaurantViewState.getTableType());
        showPopup(tableForm, tableFormController, restaurantController.getActiveTab(), position);
    }

    @Override
    public void showEditTableForm(Control control) {
        TableController tableController = restaurantController.getTableController(control);
        tableFormController.loadTable(tableController, restaurantViewState.getTableType());
        Point2D position = calculatePopupPosition(control, restaurantController.getActiveTab());
        showPopup(tableForm, tableFormController, restaurantController.getActiveTab(), position);
    }

    @Override
    public void createTable(TableParams tableParams) {
        try {
            TableView tableView = restaurantService.addTable(restaurantView, buildTable(tableParams));
            tableForm.hide();
            drawTable(tableView);
            if (tableView.isHosted()) {
                restaurantController.getTableController(tableView.getHost()).updateTable();
            }
            restaurantController.updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
//            initRestaurant();
        }
    }

    private Table.TableBuilder buildTable(TableParams params) {
        TableType tableType = restaurantViewState.getTableType();
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), restaurantController.getActiveTab());
        return restaurantService
                .tableBuilder()
                .type(tableType)
                .name(params.getName())
                .number(params.getNumber())
                .note(params.getNote())
                .guestCount(params.getGuestCount())
                .capacity(params.getCapacity())
                .visible(true)
                .coordinateX((int) position.getX())
                .coordinateY((int) position.getY())
                .dimensionX((int) params.getDimension().getWidth())
                .dimensionY((int) params.getDimension().getHeight());
    }

    @Override
    public void editTable(TableController tableController, TableParams tableParams) {
        TableView tableView = tableController.getView();
        TableView previousHost = tableView.getHost();

        try {
            setTableParams(tableView, tableParams);
            tableForm.hide();
            restaurantController.addNodeToPane(tableController.getRoot(), restaurantViewState.getTableType());
            tableController.updateTable();
            updateHostNodes(tableView, previousHost);
            restaurantController.updateRestaurantSummary();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    Resources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
//            initRestaurant();
        }
    }

    private void setTableParams(TableView tableView, TableParams tableParams) {
        if (notOwnNumberIsDisplayed(tableView, tableParams)) {
            restaurantService.setTableNumber(tableView, tableParams.getNumber(), restaurantView);
        }
        restaurantService.setTableParams(tableView, tableParams);
    }

    private boolean notOwnNumberIsDisplayed(TableView tableView, TableParams tableParams) {
        return tableView.getDisplayedNumber() != tableParams.getNumber();
    }

    private void updateHostNodes(TableView tableView, TableView previousHost) {
        if(tableView.getHost() != null) {
            restaurantController.getTableController(tableView.getHost()).updateTable();
        }
        if(previousHost != null) {
            restaurantController.getTableController(previousHost).updateTable();
        }
    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = restaurantController.getTableController(node);
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
            TableController hostController = restaurantController.getTableController(tableView.getHost());
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
        ErrorMessage.showErrorMessage(restaurantController.getActiveTab(), errorMessage + tableView.getNumber());
    }

    @Override
    public void rotateTable(Node node) {
        TableController tableController = restaurantController.getTableController(node);
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
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(), Resources.WAITER.getString("InsufficientSelection"));
            return;
        }
        TableController consumerController = selectedTables.iterator().next();
        TableView consumer = consumerController.getView();
        try {
            restaurantService.mergeTables(consumer, getConsumedTables(consumerController));
            updateConsumer(consumerController);
            updateConsumed();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(), Resources.WAITER.getString("ConsumerSelectedForMerge"));
        }
    }

    private List<TableView> getConsumedTables(TableController firstSelected) {
        List<TableView> consumed = new ArrayList<>();
        selectedTables.stream()
                .filter(tableController -> !tableController.equals(firstSelected))
                .forEach(tableController -> consumed.add(tableController.getView()));
        return consumed;
    }

    private void updateConsumer(TableController consumerController) {
        consumerController.consumeTables();
        consumerController.updateTable();
        consumerController.deselectTable();
        selectedTables.remove(consumerController);
    }

    private void updateConsumed() {
        selectedTables.forEach(tableController -> {
            tableControllers.remove(tableController);
            restaurantController.getActiveTab().getChildren().remove(tableController.getRoot());
        });
        selectedTables.clear();
    }

    @Override
    public void splitTables(Node node) {
        TableController tableController = restaurantController.getTableController(node);
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
    public void drawTable(TableView tableView) {
        TableController tableController = WaiterRegistry.getInstance(TableController.class);
        tableController.setView(tableView);
        tableControllers.add(tableController);
        viewLoader.loadView(tableController);
        addPressAndHold(tableController.getViewState(), tableController.getRoot(),
                new TableContextMenuBuilderDecorator(restaurantController, this, tableController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
        restaurantController.addNodeToPane(tableController.getRoot(), tableView.getType());
    }

    @Override
    public int getFirstUnusedTableNumber() {
        return restaurantService.getFirstUnusedNumber();
    }
}
