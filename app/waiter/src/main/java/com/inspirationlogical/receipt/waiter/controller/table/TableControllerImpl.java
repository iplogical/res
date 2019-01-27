package com.inspirationlogical.receipt.waiter.controller.table;

import com.inspirationlogical.receipt.corelib.model.enums.TableType;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.table.TableServiceConfig;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.contextmenu.BaseContextMenuBuilder;
import com.inspirationlogical.receipt.waiter.contextmenu.TableContextMenuBuilderDecorator;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleFxmlView;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.utility.CSSUtilities;
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
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.inspirationlogical.receipt.corelib.frontend.view.NodeUtility.showNode;
import static com.inspirationlogical.receipt.corelib.frontend.view.PressAndHoldHandler.addPressAndHold;
import static com.inspirationlogical.receipt.waiter.controller.table.DragAndDropHandler.addTableDragAndDrop;
import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@FXMLController
@Scope("prototype")
public class TableControllerImpl implements TableController {

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
    private ApplicationContext applicationContext;

    @Autowired
    private TableServiceConfig tableServiceConfig;

    @Autowired
    private RestaurantController restaurantController;

    @Autowired
    private TableConfigurationController tableConfigurationController;

    @Autowired
    private SaleController saleController;

    @Getter
    @Setter
    private TableView tableView;

    @Getter
    private boolean selected;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        DragAndDropHandler.setRestaurantController(restaurantController);
    }

    @Override
    public void initialize(TableView tableView) {
        setTableView(tableView);
        addTableDragAndDrop(rootTable);
        addPressAndHold(rootTable,
                applicationContext.getBean(TableContextMenuBuilderDecorator.class,this, new BaseContextMenuBuilder())
        );
        setPreferredSize();
        updateTable();
    }

    private void setPreferredSize() {
        tableStackPane.setPrefWidth(tableView.getWidth());
        tableStackPane.setPrefHeight(tableView.getHeight());
    }

    @Override
    public boolean isOpen() {
        return tableView.isOpen();
    }

    @Override
    public int getTableNumber() {
        return tableView.getNumber();
    }

    @Override
    public TableType getTableType() {
        return tableView.getType();
    }

    @Override
    public Control getRoot() {
        return rootTable;
    }

    @Override
    public void updateTable() {
        updateTableParams();
        CSSUtilities.setBackgroundColor(tableView, tableServiceConfig.getRecentConsumption(tableView.getNumber()), tableStackPane);
        showNode(rootTable, new Point2D(tableView.getCoordinateX(), tableView.getCoordinateY()));
    }

    private void updateTableParams() {
        setPreferredSize();
        name.setText(tableView.getName());
        number.setText(valueOf(tableView.getNumber()));
        guests.setText(valueOf(tableView.getGuestCount()));
        capacity.setText(valueOf(tableView.getCapacity()));
        note.setVisible(isNotEmpty(tableView.getNote()));
    }

    @Override
    public void openTable(Control control) {
        tableView = tableServiceConfig.openTable(tableView.getNumber());
        updateTable();
    }

    @Override
    public void reOpenTable(Control control) {
        tableView = tableServiceConfig.reOpenTable(tableView.getNumber());
        updateTable();
    }

    @Override
    public void deselectTable() {
        selected = false;
        CSSUtilities.setBorderColor(false, tableStackPane);
    }

    @Override
    public void setOrderDelivered(boolean delivered) {
        tableView = tableServiceConfig.setOrderDelivered(tableView.getNumber(), delivered);
        if (delivered) {
            LocalDateTime now = LocalDateTime.now();
            tableView = tableServiceConfig.setOrderDeliveredTime(tableView.getNumber(), now);
        }
    }

    @Override
    public LocalDateTime getOrderDeliveryTime() {
        return tableView.getOrderDeliveryTime();
    }

    @FXML
    public void onTableClicked(MouseEvent event) {
        if (isContextMenuOpen() || restaurantController.isMotionMode()) {
            moveTables();
        } else if (restaurantController.isConfigurationMode()) {
            invertSelectionState();
        } else {
            if (tableServiceConfig.isTableOpen(tableView.getNumber())) {
                enterSaleView();
            }
        }
    }

    private boolean isContextMenuOpen() {
        return rootTable.getContextMenu() != null && rootTable.getContextMenu().isShowing();
    }

    private void moveTables() {
        tableConfigurationController.moveTable(this);
    }

    private void invertSelectionState() {
        selected = !selected;
        CSSUtilities.setBorderColor(selected, tableStackPane);
    }

    private void enterSaleView() {
        saleController.setTableView(tableView);
        WaiterApp.showView(SaleFxmlView.class);
        saleController.enterSaleView();
    }

    @Override
    public Node getRootNode() {
        return rootTable;
    }
}
