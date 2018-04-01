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
import java.util.function.Predicate;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.*;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.HOLD_DURATION_MILLIS;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static java.util.stream.Collectors.toList;

//@Singleton
@Component
public class TableConfigurationControllerImpl implements TableConfigurationController {

    @Autowired
    private RestaurantController restaurantController;

    private RestaurantViewState restaurantViewState;

    private static Predicate<TableView> DISPLAYABLE_TABLE = TableView::isDisplayable;

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
        List<TableView> tables = restaurantService.getTables();
        tables.sort(Comparator.comparing(TableView::isConsumed));   // Put consumed tables to the end so the consumer is loaded in advance.
        tables.stream().filter(DISPLAYABLE_TABLE).forEach(this::drawTable);
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
            updateHostTable(tableView);
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
        }
    }

    private TableParams buildTableParams(TableParams params) {
        TableType tableType = restaurantViewState.getTableType();
        Point2D position = calculateTablePosition(tableFormController.getRootNode(), restaurantController.getActiveTab());
        params.setType(tableType);
        params.setPosition(position);
        return params;
    }

    private void updateHostTable(TableView tableView) {
        if (tableView.isHosted()) {
            getTableController(tableView.getHost()).updateTable();
        }
    }

    @Override
    public void editTable(TableController tableController, TableParams tableParams) {
        TableView tableView = tableController.getView();
        TableView previousHost = tableView.getHost();

        try {
            setTableParams(tableView, tableParams);
            hideTableForm();
            restaurantController.addNodeToPane(tableController.getRoot(), restaurantViewState.getTableType());
            tableController.updateTable();
            updateHostNodes(tableView, previousHost);
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableAlreadyUsed") + tableParams.getNumber());
        }
    }

    private void setTableParams(TableView tableView, TableParams tableParams) {
        if (notOwnNumberIsDisplayed(tableView, tableParams)) {
            restaurantService.setTableNumber(tableView, tableParams.getNumber());
        }
        restaurantService.setTableParams(tableView, tableParams);
    }

    private boolean notOwnNumberIsDisplayed(TableView tableView, TableParams tableParams) {
        return tableView.getDisplayedNumber() != tableParams.getNumber();
    }

    private void updateHostNodes(TableView tableView, TableView previousHost) {
        if(tableView.getHost() != null) {
            getTableController(tableView.getHost()).updateTable();
        }
        if(previousHost != null) {
            getTableController(previousHost).updateTable();
        }
    }

    @Override
    public void deleteTable(Node node) {
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        restaurantService.deleteTable(tableView);
        removeNode((Pane) node.getParent(), node);
        tableControllers.remove(tableController);
        updateHostTable(tableView);
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
        filterNormalSelectedTables();
        if(insufficientSelection()) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(), WaiterResources.WAITER.getString("InsufficientSelection"));
            return;
        }
        TableController consumerController = selectedTables.get(0);
        try {
            List<TableView> consumedTables = getConsumedTables(consumerController);
            restaurantService.mergeTables(consumerController.getView(), consumedTables);
            updateConsumer(consumerController);
            updateConsumed();
        } catch (IllegalTableStateException e) {
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(), WaiterResources.WAITER.getString("ConsumerSelectedForMerge"));
        }
    }

    private boolean insufficientSelection() {
        return selectedTables.size() < 2;
    }

    private void filterNormalSelectedTables() {
        selectedTables = selectedTables.stream()
                .filter(tableController -> tableController.getView().getType().equals(TableType.NORMAL))
                .collect(toList());
    }

    private List<TableView> getConsumedTables(TableController consumerController) {
        List<TableView> consumed = new ArrayList<>();
        selectedTables.stream()
                .filter(tableController -> !tableController.equals(consumerController))
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
        TableController tableController = getTableController(node);
        TableView tableView = tableController.getView();
        List<TableView> tables = restaurantService.splitTables(tableView);
        tables.forEach(this::drawTable);
        tableController.releaseConsumedTables();
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
//            viewLoader.loadViewIntoScene(restaurantController);
            WaiterApp.showView(RestaurantFxmlView.class);
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableDoesNotExist") + reservation.getTableNumber());
            return;
        }
        TableController tableController = filteredControllers.get(0);
        TableView tableView = tableController.getView();
        if(retailService.isTableOpen(tableView)) {
//            viewLoader.loadViewIntoScene(restaurantController);
            WaiterApp.showView(RestaurantFxmlView.class);
            ErrorMessage.showErrorMessage(restaurantController.getActiveTab(),
                    WaiterResources.WAITER.getString("TableIsOpenReservation") + tableView.getNumber());
            return;
        }
        TableParams tableParams = buildTableParams(reservation, tableView);
        editTable(tableController, tableParams);
        tableController.openTable(null);
//        viewLoader.loadViewIntoScene(restaurantController);
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    private TableParams buildTableParams(ReservationView reservation, TableView tableView) {
        return TableParams.builder()
                .name(reservation.getName())
                .number(Integer.valueOf(reservation.getTableNumber()))
                .note(reservation.getNote())
                .guestCount(Integer.valueOf(reservation.getGuestCount()))
                .capacity(tableView.getCapacity())
                .dimension(tableView.getDimension())
                .build();
    }

    @Override
    public void drawTable(TableView tableView) {
        tableViewBeingDrawn = tableView;
//        TableController tableController = appCtx.getBean(TableController.class);
//        tableController.setView(tableView);
//        tableControllers.add(tableController);

        WaiterApp.showView(TableFxmlView.class);

//        viewLoader.loadView(tableController);
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
