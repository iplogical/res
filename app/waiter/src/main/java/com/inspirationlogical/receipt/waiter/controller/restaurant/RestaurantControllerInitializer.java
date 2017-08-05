package com.inspirationlogical.receipt.waiter.controller.restaurant;

import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.RestaurantContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.Comparator;
import java.util.List;

import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantControllerImpl.DISPLAYABLE_TABLE;
import static com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantControllerImpl.HOLD_DURATION_MILLIS;
import static com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantControllerImpl.INITIAL_GRID_SIZE;
import static java.util.stream.Collectors.toList;

public class RestaurantControllerInitializer {
    private RestaurantControllerImpl r;

    RestaurantControllerInitializer(RestaurantControllerImpl r) {
        this.r = r;
    }

    public void initialize() {
        initContextMenu(r.tablesControl);
        initContextMenu(r.loiterersControl);
        initContextMenu(r.frequentersControl);
        initContextMenu(r.employeesControl);
        initTableForm();
        initControls();
        initRestaurant();
        initTableConfigurationController();
        r.initLiveTime(r.liveTime);
    }

    private void initContextMenu(Label control) {
        addPressAndHold(r.restaurantViewState, control,
                new RestaurantContextMenuBuilderDecorator(r, r.tableConfigurationController, new BaseContextMenuBuilder()),
                Duration.millis(HOLD_DURATION_MILLIS));
    }

    private void initTableForm() {
        r.tableForm = new Popup();
        r.tableForm.getContent().add(r.viewLoader.loadView(r.tableFormController));
    }

    private void initControls() {
        r.restaurantViewState.setConfigurable(r.configuration.selectedProperty());
        r.restaurantViewState.getMotionViewState().setMovableProperty(r.motion.selectedProperty());
        r.restaurantViewState.getMotionViewState().setSnapToGridProperty(r.snapToGrid.selectedProperty());
        r.restaurantViewState.getMotionViewState().setGridSizeProperty(r.setGridSize.valueProperty());
        r.snapToGrid.disableProperty().bind(r.motion.selectedProperty().not());
        r.setGridSize.disableProperty().bind(r.motion.selectedProperty().not());
        r.getGridSize.textProperty().bind(Bindings.format("%.0f", r.setGridSize.valueProperty()));
        r.setGridSize.setValue(INITIAL_GRID_SIZE);
    }

    private void initRestaurant() {
        r.restaurantView = r.restaurantService.getActiveRestaurant();
        initTables();
    }

    private void initTables() {
        r.tablesTab.getChildren().removeAll(r.tableControllers.stream()
                .filter(tableController -> tableController.getView().isNormal() || tableController.getView().isConsumer())
                .map(TableController::getRoot).collect(toList()));
        r.loiterersTab.getChildren().removeAll(r.tableControllers.stream()
                .filter(tableController -> tableController.getView().isLoiterer())
                .map(TableController::getRoot).collect(toList()));
        r.frequentersTab.getChildren().removeAll(r.tableControllers.stream()
                .filter(tableController -> tableController.getView().isFrequenter())
                .map(TableController::getRoot).collect(toList()));
        r.employeesTab.getChildren().removeAll(r.tableControllers.stream()
                .filter(tableController -> tableController.getView().isEmployee())
                .map(TableController::getRoot).collect(toList()));
        r.tableControllers.clear();
        List<TableView> tables = r.restaurantService.getTables();
        tables.sort(Comparator.comparing(TableView::isConsumed));   // Put consumed tables to the end so the consumer is loaded in advance.
        tables.stream().filter(DISPLAYABLE_TABLE).forEach(r::drawTable);
    }


    private void initTableConfigurationController() {
        r.tableConfigurationController.setRestaurantController(r);
        r.tableConfigurationController.setViewLoader(r.viewLoader);
        r.tableConfigurationController.setRestaurantViewState(r.restaurantViewState);
        r.tableConfigurationController.initialize();
    }
}
