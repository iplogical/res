package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.google.inject.Inject;
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
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.stage.Popup;
import lombok.Setter;

import java.util.Set;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculatePopupPosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.calculateTablePosition;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showPopup;

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
            restaurantController.drawTable(tableView);
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

}
