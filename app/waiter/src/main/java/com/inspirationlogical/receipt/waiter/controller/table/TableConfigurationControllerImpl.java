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
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.exception.ViewNotFoundException;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.*;
import static java.util.stream.Collectors.toList;

@Component
public class TableConfigurationControllerImpl implements TableConfigurationController {

    @Autowired
    private RestaurantController restaurantController;

    @Autowired
    private TableFormController tableFormController;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private RetailService retailService;

    @Getter
    private Set<TableController> tableControllers;

    private RestaurantView restaurantView;
    private Popup tableForm;

    @PostConstruct
    private void init() {
        tableControllers = new HashSet<>();
    }

    @Override
    public void initialize() {
        restaurantView = restaurantService.getActiveRestaurant();
        initTables();
        initTableForm();
    }

    private void initTables() {
        List<TableView> tables = restaurantService.getDisplayableTables();
        tables.forEach(this::drawTable);
    }

    @Override
    public void showCreateTableForm(Point2D position) {
        tableFormController.createTableForm(restaurantController.getTableType());
        showPopup(tableForm, tableFormController, restaurantController.getActiveTab(), position);
    }

    @Override
    public void showEditTableForm(Control control) {
        TableController tableController = getTableController(control);
        tableFormController.loadTableForm(tableController, restaurantController.getTableType());
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
        TableType tableType = restaurantController.getTableType();
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), restaurantController.getActiveTab());
        params.setType(tableType);
        params.setPositionX((int)position.getX());
        params.setPositionY((int)position.getY());
        return params;
    }

    @Override
    public void editTable(TableController tableController, TableParams tableParams) {
        try {
            TableView tableView = restaurantService.updateTableParams(tableController.getTableNumber(), tableParams);
            hideTableForm();
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
        restaurantService.deleteTable(tableView);removeNode((Pane) node.getParent(), node);
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
        List<TableController> tablesToExchange = tableControllers.stream()
                .filter(TableController::isSelected)
                .filter(tableController -> tableController.getView().getType().equals(TableType.NORMAL))
                .collect(toList());
        if(tablesToExchange.size() != 2) {
            ErrorMessage.showErrorMessage(restaurantController.getRootNode(),
                    WaiterResources.WAITER.getString("TableConfiguration.InsufficientForExchange"));
            return;
        }
        exchangeTables(tablesToExchange);
    }

    private void exchangeTables(List<TableController> tablesToExchange) {
        TableController selected = tablesToExchange.get(0);
        TableController other = tablesToExchange.get(1);
        List<TableView> tableViewList = restaurantService.exchangeTables(selected.getTableNumber(), other.getTableNumber());
        selected.setView(tableViewList.get(0));
        other.setView(tableViewList.get(1));
        tablesToExchange.forEach(TableController::updateTable);
    }

    @Override
    public void clearSelections() {
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
        TableController tableController = (TableController) WaiterApp.getController(TableFxmlView.class);
        tableController.initialize(tableView);
        tableControllers.add(tableController);
        restaurantController.addNodeToPane(tableController.getRoot(), tableView.getType());
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
