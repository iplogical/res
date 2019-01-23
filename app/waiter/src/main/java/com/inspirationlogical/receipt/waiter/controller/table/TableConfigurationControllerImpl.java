package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.exception.IllegalTableStateException;
import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.ReservationView;
import com.inspirationlogical.receipt.corelib.model.view.RestaurantView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.params.TableParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantViewState;
import com.inspirationlogical.receipt.waiter.exception.ViewNotFoundException;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.util.Duration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.*;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.HOLD_DURATION_MILLIS;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static java.util.stream.Collectors.toList;

@Component
public class TableConfigurationControllerImpl implements TableConfigurationController {

    @Autowired
    private RestaurantController restaurantController;

    private RestaurantViewState restaurantViewState;

    @Autowired
    private TableFormController tableFormController;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RetailService retailService;

    @Getter
    private Set<TableController> tableControllers;
    private List<TableController> selectedTables;

    private RestaurantView restaurantView;
    private Popup tableForm;

    private TableView tableViewBeingDrawn;

    private TableController tableControllerBeingDrawn;

    @PostConstruct
    private void init() {
        tableControllers = new HashSet<>();
        selectedTables = new ArrayList<>();
    }

    @Override
    public void initialize() {
        restaurantView = restaurantService.getActiveRestaurant();
        restaurantViewState = restaurantController.getViewState();
        initTables();
        initTableForm();
    }

    private void initTables() {
        List<TableView> tables = restaurantService.getDisplayableTables();
        tables.forEach(this::drawTable);
    }

    @Override
    public void showCreateTableForm(Point2D position) {
        tableFormController.createTableForm(restaurantViewState.getTableType());
        showPopup(tableForm, tableFormController, restaurantController.getActiveTab(), position);
    }

    @Override
    public void showEditTableForm(Control control) {
        TableController tableController = getTableController(control);
        tableFormController.loadTableForm(tableController, restaurantViewState.getTableType());
        Point2D position = calculatePopupPosition(control, restaurantController.getActiveTab());
        showPopup(tableForm, tableFormController, restaurantController.getActiveTab(), position);
    }

    private void initTableForm() {
        tableForm = new Popup();
        tableForm.getContent().add(WaiterApp.getRootNode(TableFormFxmlView.class));
    }

    @Override
    public void hideTableForm() {
        tableForm.hide();
    }

    @Override
    public void addTable(TableParams tableParams) {
        try {
            TableView tableView = restaurantService.addTable(restaurantView, buildTableParams(tableParams));
            hideTableForm();
            drawTable(tableView);
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
        }
    }

    private TableParams buildTableParams(TableParams params) {
        TableType tableType = restaurantViewState.getTableType();
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), restaurantController.getActiveTab());
        params.setType(tableType);
        params.setPositionX((int)position.getX());
        params.setPositionY((int)position.getY());
        return params;
    }

    @Override
    public void editTable(TableController tableController, TableParams tableParams) {

        try {
            TableView tableView = restaurantService.setTableParams(tableController.getTableNumber(), tableParams);
            hideTableForm();
            restaurantController.addNodeToPane(tableController.getRoot(), restaurantViewState.getTableType());
            tableController.setView(tableView);
            tableController.updateTable();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
        }
    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        restaurantService.deleteTable(tableView);
        removeNode((Pane) node.getParent(), node);
        tableControllers.remove(tableController);
    }

    @Override
    public void rotateTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = restaurantService.rotateTable(tableController.getView().getNumber());
        tableController.setView(tableView);
        tableController.updateTable();
    }

    @Override
    public void moveTable(TableController tableController) {
        Node view = tableController.getRoot();
        Point2D position = new Point2D(view.getLayoutX(), view.getLayoutY());
        TableView tableView = restaurantService.setTablePosition(tableController.getView().getNumber(), position);
        tableController.setView(tableView);
        tableController.updateTable();
    }

    @Override
    public void exchangeTables() {
        List<TableView> tablesToExchange = selectedTables.stream()
                .map(TableController::getView)
                .collect(toList());
        if(selectedTables.size() != 2) {
            ErrorMessage.showErrorMessage(restaurantController.getRootNode(),
                    WaiterResources.WAITER.getString("TableConfiguration.InsufficientForExchange"));
            selectedTables.clear();
            return;
        }
        restaurantService.exchangeTables(tablesToExchange.get(0), tablesToExchange.get(1));
        selectedTables.forEach(TableController::updateTable);
        selectedTables.clear();
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
    public boolean hasSelection() {
        return selectedTables.size() > 1;
    }

    @Override
    public void clearSelections() {
        selectedTables.clear();
        tableControllers.forEach(TableController::deselectTable);
    }

    @Override
    public void openTableOfReservation(ReservationView reservation) {
        List<TableController> filteredControllers = tableControllers.stream()
                .filter(controller -> controller.getView().getNumber() == Integer.valueOf(reservation.getTableNumber()))
                .limit(1)
                .collect(toList());
        if(filteredControllers.isEmpty()) {
            WaiterApp.showView(RestaurantFxmlView.class);
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableDoesNotExist") + reservation.getTableNumber());
            return;
        }
        TableController tableController = filteredControllers.get(0);
        TableView tableView = tableController.getView();
        if(retailService.isTableOpen(tableView)) {
            WaiterApp.showView(RestaurantFxmlView.class);
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableIsOpenReservation") + tableView.getNumber());
            return;
        }
        TableParams tableParams = buildTableParams(reservation, tableView);
        editTable(tableController, tableParams);
        tableController.openTable(null);
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    private TableParams buildTableParams(ReservationView reservation, TableView tableView) {
        return TableParams.builder()
                .name(reservation.getName())
                .number(Integer.valueOf(reservation.getTableNumber()))
                .note(reservation.getNote())
                .guestCount(Integer.valueOf(reservation.getGuestCount()))
                .capacity(tableView.getCapacity())
                .width(tableView.getWidth())
                .height(tableView.getHeight())
                .build();
    }

    @Override
    public void drawTable(TableView tableView) {
        tableViewBeingDrawn = tableView;
        WaiterApp.showView(TableFxmlView.class);
        addPressAndHold(tableControllerBeingDrawn.getViewState(), tableControllerBeingDrawn.getRoot(),
                new TableContextMenuBuilderDecorator(this, tableControllerBeingDrawn, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
        restaurantController.addNodeToPane(tableControllerBeingDrawn.getRoot(), tableView.getType());
    }

    @Override
    public TableView getTableViewBeingDrawn() {
        return tableViewBeingDrawn;
    }

    @Override
    public void setTableControllerBeingDrawn(TableController tableControllerBeingDrawn) {
        this.tableControllerBeingDrawn = tableControllerBeingDrawn;
    }

    @Override
    public TableController getTableController(TableView tableView) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getView().getNumber() == tableView.getNumber())
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table view could not be found"));
    }

    private TableController getTableController(Node node) {
        return tableControllers
                .stream()
                .filter(tableController -> tableController.getRoot().equals(node))
                .findFirst()
                .orElseThrow(() -> new ViewNotFoundException("Table root could not be found"));
    }
}
