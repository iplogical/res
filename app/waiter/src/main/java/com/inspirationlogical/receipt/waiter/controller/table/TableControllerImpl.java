package com.inspirationlogical.receipt.waiter.controller.table;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.frontend.viewstate.ViewState;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.hideNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Component
public class TableControllerImpl implements TableController {

    private static final String TABLE_VIEW_PATH = "/view/fxml/Table.fxml";
    private static final String CONSUMED_VIEW_PATH = "/view/fxml/ConsumedTable.fxml";

    @FXML
    private Label rootTable;
    @FXML
    private AnchorPane tableContainer;
    @FXML
    private StackPane tableStackPane;
    @FXML
    private Label name;
    @FXML
    private Label guests;
    @FXML
    private Label number;
    @FXML
    private Label capacity;
    @FXML
    private ImageView note;
    @FXML
    private ImageView meeple;
    @FXML
    private Label hostedCount;

    private @Inject ViewLoader viewLoader;

    private @Inject RetailService retailService;

    private @Inject RestaurantController restaurantController;
    private @Inject TableConfigurationController tableConfigurationController;
    private @Inject SaleController saleController;

    private TableView tableView;

    private TableViewState tableViewState;

    public void setView(TableView tableView) {
        this.tableView = tableView;
        this.tableViewState = new TableViewState(restaurantController.getViewState(), tableView);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addDragAndDrop(rootTable, tableViewState.getRestaurantViewState().getMotionViewState());
        setPreferredSize();
        updateTable();
    }

    private void setPreferredSize() {
        tableStackPane.setPrefWidth(tableView.getDimension().getWidth());
        tableStackPane.setPrefHeight(tableView.getDimension().getHeight());
    }

    @Override
    public void consumeTables() {
        releaseConsumedTables();
        tableView.getConsumedTables().forEach(view -> {
            StackPane consumedTable = buildConsumedTable(view);
            tableContainer.getChildren().add(consumedTable);
        });
    }

    private StackPane buildConsumedTable(TableView view) {
        StackPane consumedTable = (StackPane) viewLoader.loadView(CONSUMED_VIEW_PATH);
        setConsumedTableNumber(view, consumedTable);
        setConsumedTableSize(view, consumedTable);
        setConsumedTableHostInfo(view, consumedTable);
        return consumedTable;
    }

    private void setConsumedTableNumber(TableView view, StackPane consumedRoot) {
        Label consumedNumber = (Label) consumedRoot.lookup("#number");
        consumedNumber.setText(valueOf(view.getNumber()));
    }

    private void setConsumedTableSize(TableView view, StackPane consumedRoot) {
        consumedRoot.setPrefWidth((int) view.getDimension().getWidth());
        consumedRoot.setPrefHeight((int) view.getDimension().getHeight());
        Point2D position = view.getPosition().subtract(tableView.getPosition());
        consumedRoot.setLayoutX((int) position.getX());
        consumedRoot.setLayoutY((int) position.getY());
    }

    private void setConsumedTableHostInfo(TableView view, StackPane consumedRoot) {
        Label consumedHosted = (Label) consumedRoot.lookup("#hostedCount");
        ImageView consumedMeeple = (ImageView) consumedRoot.lookup("#meeple");
        int numberOfHostedTable = view.getHostedTables().size();
        if (numberOfHostedTable > 0) {
            consumedMeeple.setVisible(true);
            consumedHosted.setVisible(true);
            consumedHosted.setText(valueOf(numberOfHostedTable));
        } else {
            consumedMeeple.setVisible(false);
            consumedHosted.setVisible(false);
        }
    }

    @Override
    public TableView getView() {
        return tableView;
    }

    @Override
    public Control getRoot() {
        return rootTable;
    }

    @Override
    public ViewState getViewState() {
        return tableViewState;
    }

    @Override
    public void updateTable() {
        if (tableView.isVisible()) {
            if(tableView.isConsumer()) {
                consumeTables();
            }
            updateTableParams();
            CSSUtilities.setBackgroundColor(tableViewState, tableStackPane);
            showNode(rootTable, tableView.getPosition());
        } else {
            hideNode(rootTable);
            if (tableView.isConsumed()) {
                tableConfigurationController.getTableController(tableView.getConsumer()).updateTable();
            }
        }
    }

    private void updateTableParams() {
        setPreferredSize();
        name.setText(tableView.getName());
        updateHostInfo();
        updateGuestCountAndCapacity();
        setNoteVisibility();
    }

    private void updateGuestCountAndCapacity() {
        if (tableView.isConsumer()) {
            setAggregateGuestCountAndCapacity();
        } else {
            guests.setText(valueOf(tableView.getGuestCount()));
            capacity.setText(valueOf(tableView.getCapacity()));
        }
    }

    private void setAggregateGuestCountAndCapacity() {
        Integer tableGuests = tableView.getGuestCount();
        Integer tableCapacity = tableView.getCapacity();
        for(TableView consumed : tableView.getConsumedTables()) {
            tableGuests += consumed.getGuestCount();
            tableCapacity += consumed.getCapacity();
        }
        guests.setText(valueOf(tableGuests));
        capacity.setText(valueOf(tableCapacity));
    }

    private void updateHostInfo() {
        setHostInfoVisibility(tableView.isHost());
        number.setText(tableView.isHosted() ? valueOf(tableView.getHost().getNumber()): valueOf(tableView.getNumber()));
    }

    private void setHostInfoVisibility(boolean isHost) {
        meeple.setVisible(isHost);
        setHostedCountVisibilityAndText(isHost);
    }

    private void setHostedCountVisibilityAndText(boolean isHost) {
        hostedCount.setVisible(isHost);
        hostedCount.setText(isHost ? valueOf(tableView.getHostedTables().size()): EMPTY);
    }

    private void setNoteVisibility() {
        note.setVisible(isNotEmpty(tableView.getNote()));
    }

    @Override
    public void releaseConsumedTables() {
        tableContainer.getChildren().clear();
        tableContainer.getChildren().add(tableStackPane);
    }

    @Override
    public void openTable(Control control) {
        retailService.openTable(tableView);
        updateTable();
    }

    @Override
    public void reOpenTable(Control control) {
        if (retailService.reOpenTable(tableView)) {
            updateTable();
        } else {
            ErrorMessage.showErrorMessage(restaurantController.getRootNode(),
                    WaiterResources.WAITER.getString("Restaurant.ReOpenTableFailed"));
        }
    }

    @Override
    public void deselectTable() {
        tableViewState.setSelected(false);
        CSSUtilities.setBorderColor(tableViewState.isSelected(), tableStackPane);
    }

    @Override
    public void setOrderDelivered(boolean delivered) {
        tableViewState.setOrderDelivered(delivered);
        retailService.setOrderDelivered(tableView, delivered);
        if (delivered) {
            LocalDateTime now = LocalDateTime.now();
            tableViewState.setOrderDeliveredTime(now);
            retailService.setOrderDeliveredTime(tableView, now);
        }
    }

    @Override
    public LocalDateTime getOrderDeliveredTime() {
        return tableViewState.getOrderDeliveredTime();
    }

    @FXML
    public void onTableClicked(MouseEvent event) {
        if(isContextMenuOpen() || isMotionMode()) {
            moveTables();
        } else if(isConfigurationMode()) {
            invertSelectionState();
        } else {
            if(tableView.isOpen()) {
                enterSaleView();
            }
        }
    }

    private void moveTables() {
        moveConsumedTables();
        tableConfigurationController.moveTable(this);
    }

    private void moveConsumedTables() {
        for (TableView view : tableView.getConsumedTables()) {
            Point2D delta = new Point2D(rootTable.getLayoutX(), rootTable.getLayoutY()).subtract(tableView.getPosition());
            tableConfigurationController.moveTable(view, view.getPosition().add(delta));
        }
    }

    private void invertSelectionState() {
        tableViewState.setSelected(!tableViewState.isSelected());
        tableConfigurationController.selectTable(this, tableViewState.isSelected());
        CSSUtilities.setBorderColor(tableViewState.isSelected(), tableStackPane);
    }

    private void enterSaleView() {
        saleController.setTableView(tableView);
        viewLoader.loadViewIntoScene(saleController);
        saleController.enterSaleView();
    }

    private Boolean isMotionMode() {
        return tableViewState.getRestaurantViewState().getMotionViewState().getMovableProperty().getValue();
    }

    private Boolean isConfigurationMode() {
        return tableViewState.getRestaurantViewState().getConfigurable().getValue();
    }

    private boolean isContextMenuOpen() {
        return rootTable.getContextMenu() != null && rootTable.getContextMenu().isShowing();
    }

    @Override
    public String getViewPath() {
        return TABLE_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return rootTable;
    }
}
