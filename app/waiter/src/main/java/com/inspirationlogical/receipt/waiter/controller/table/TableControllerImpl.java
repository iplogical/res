package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleFxmlView;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.DragAndDropHandler.addDragAndDrop;
import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@FXMLController
@Scope("prototype")
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

    @Autowired
    private RetailService retailService;

    @Autowired
    private RestaurantController restaurantController;

    @Autowired
    private TableConfigurationController tableConfigurationController;

    @Autowired
    private SaleController saleController;

    private TableView tableView;

    private TableViewState tableViewState;

    public void setView(TableView tableView) {
        this.tableView = tableView;
        this.tableViewState = new TableViewState(restaurantController.getViewState(), tableView, retailService.isTableOpen(tableView));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setView(tableConfigurationController.getTableViewBeingDrawn());
        addDragAndDrop(rootTable, tableViewState.getRestaurantViewState().getMotionViewState());
        setPreferredSize();
        updateTable();

    }

    @PostConstruct
    private void init() {
        tableConfigurationController.getTableControllers().add(this);
        tableConfigurationController.setTableControllerBeingDrawn(this);
    }

    private void setPreferredSize() {
        tableStackPane.setPrefWidth(tableView.getHeight());
        tableStackPane.setPrefHeight(tableView.getHeight());
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
    public TableViewState getViewState() {
        return tableViewState;
    }

    @Override
    public void updateTable() {
        updateTableParams();
        CSSUtilities.setBackgroundColor(tableViewState, retailService.getRecentConsumption(tableView), tableStackPane);
        showNode(rootTable, new Point2D(tableView.getCoordinateX(), tableView.getCoordinateY()));
    }

    private void updateTableParams() {
        setPreferredSize();
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getCapacity()));
        note.setVisible(isNotEmpty(tableView.getNote()));
        tableViewState.setOpen(retailService.isTableOpen(tableView));
    }


    @Override
    public void openTable(Control control) {
        retailService.openTable(tableView);
        tableViewState.setOpen(true);
        updateTable();
    }

    @Override
    public void reOpenTable(Control control) {
        if (retailService.reOpenTable(tableView)) {
            tableViewState.setOpen(true);
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
        if (isContextMenuOpen() || isMotionMode()) {
            moveTables();
        } else if (isConfigurationMode()) {
            invertSelectionState();
        } else {
            if (retailService.isTableOpen(tableView)) {
                enterSaleView();
            }
        }
    }

    private void moveTables() {
        tableConfigurationController.moveTable(this);
    }

    private void invertSelectionState() {
        tableViewState.setSelected(!tableViewState.isSelected());
        tableConfigurationController.selectTable(this, tableViewState.isSelected());
        CSSUtilities.setBorderColor(tableViewState.isSelected(), tableStackPane);
    }

    private void enterSaleView() {
        saleController.setTableView(tableView);
        WaiterApp.showView(SaleFxmlView.class);
//        viewLoader.loadViewIntoScene(saleController);
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
