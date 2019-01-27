package com.inspirationlogical.receipt.waiter.controller.reatail;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import com.inspirationlogical.receipt.corelib.service.table.TableServiceConfig;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.input.MouseEvent;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by Bálint on 2017.03.28..
 */
@Component
public abstract class AbstractRetailControllerImpl extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractRetailControllerImpl.class);

    @FXML
    protected Label tableName;
    @FXML
    protected Label tableNumber;
    @FXML
    protected Label totalPrice;
    @FXML
    protected Label note;
    @FXML
    protected Button guestPlus;
    @FXML
    protected Button guestMinus;

    @FXML
    private javafx.scene.control.TableView<SoldProductViewModel> soldProductsTable;
    @FXML
    protected TableColumn<SoldProductViewModel, String> productName;
    @FXML
    private TableColumn<SoldProductViewModel, String> productQuantity;
    @FXML
    private TableColumn<SoldProductViewModel, String> productUnitPrice;
    @FXML
    private TableColumn<SoldProductViewModel, String> productTotalPrice;

    @Autowired
    private ReceiptService receiptService;

    @Autowired
    private TableServiceConfig tableServiceConfig;

    @Autowired
    protected RetailService retailService;

    @Autowired
    protected RestaurantController restaurantController;

    @Autowired
    protected TableConfigurationController tableConfigurationController;

    protected @Setter TableView tableView;

    protected ReceiptView receiptView;

    protected Collection<ReceiptRecordView> soldProductsView;

    protected ObservableList<SoldProductViewModel> soldProductsModel = FXCollections.observableArrayList();

    @FXML
    public void onBackToRestaurantView(Event event) {
        logger.info("Back to restaurant view from table: " + tableView.toString());
        retailService.mergeReceiptRecords(receiptView);
        backToRestaurantView();
    }

    protected void backToRestaurantView() {
        tableConfigurationController.getTableController(tableView.getNumber()).updateTable();
        WaiterApp.showView(RestaurantFxmlView.class);
    }

    @FXML
    public void onGuestPlus(Event event) {
        tableView = tableServiceConfig.setGuestCount(tableView.getNumber(), tableView.getGuestCount() + 1);
        updateTableSummary();
        tableConfigurationController.getTableController(tableView.getNumber()).setTableView(tableView);
        logger.info("The guest plus button was clicked. Guest count: " + tableView.getGuestCount());
    }

    @FXML
    public void onGuestMinus(Event event) {
        if(tableView.getGuestCount() == 0) {
            return;
        }
        tableView = tableServiceConfig.setGuestCount(tableView.getNumber(), tableView.getGuestCount() - 1);
        updateTableSummary();
        tableConfigurationController.getTableController(tableView.getNumber()).setTableView(tableView);
        logger.info("The guest minus button was clicked. Guest count: " + tableView.getGuestCount());
    }

    protected void initializeSoldProducts() {
        initializeSoldProductsTable();
        enableSoldProductsTableRowClickHandler();
    }

    private void initializeSoldProductsTable() {
        soldProductsTable.setEditable(true);
        initColumn(productName, SoldProductViewModel::getProductName);
        initColumn(productQuantity, SoldProductViewModel::getProductQuantityWithRecent);
        initColumn(productUnitPrice, SoldProductViewModel::getProductUnitPrice);
        initColumn(productTotalPrice, SoldProductViewModel::getProductTotalPrice);
    }

    protected void enableSoldProductsTableRowClickHandler() {
        soldProductsTable.setRowFactory(tv -> {
            TableRow<SoldProductViewModel> row = new TableRow<>();
            row.setOnMouseClicked(enableRowClickHandler(row));
            return row;
        });
    }

    private EventHandler<MouseEvent> enableRowClickHandler(TableRow<SoldProductViewModel> row) {
        return event -> {
            if(event.getClickCount() == 1 && (!row.isEmpty())) {
                soldProductsRowClickHandler(row.getItem());
            }
        };
    }

    protected void disableSoldProductsTableRowClickHandler() {
        soldProductsTable.setRowFactory(tv -> {
            TableRow<SoldProductViewModel> row = new TableRow<>();
            row.setOnMouseClicked(disableRowClickHandler(row));
            return row;
        });
    }

    private EventHandler<MouseEvent> disableRowClickHandler(TableRow<SoldProductViewModel> row) {
        return event -> {};
    }

    protected abstract void soldProductsRowClickHandler(SoldProductViewModel row);

    protected void updateTableSummary() {
        tableName.setText(tableView.getName());
        tableNumber.setText(tableView.getNumber() + getGuestPerCapacity());
        note.setText(tableView.getNote());
    }

     private String getGuestPerCapacity() {
        return " (" + tableView.getGuestCount() + "/" + tableView.getCapacity() + ")";
    }

    private ObservableList<SoldProductViewModel> convertReceiptRecordViewsToModel(Collection<ReceiptRecordView> soldProducts) {
        List<SoldProductViewModel> list = soldProducts.stream()
                .map(receiptRecordView -> new SoldProductViewModel(receiptRecordView, getOrderDeliveredTime()))
                .collect(toList());
        return FXCollections.observableArrayList(list);
    }

    protected LocalDateTime getOrderDeliveredTime() {
        if(getTableController() == null) {
            return null;
        }
        return getTableController().getOrderDeliveryTime();
    }

    protected void refreshSoldProductsTable() {
        soldProductsModel.clear();
        soldProductsModel.addAll(convertReceiptRecordViewsToModel(soldProductsView));
        soldProductsTable.setItems(soldProductsModel);
        soldProductsTable.refresh();
        updateSoldTotalPrice();
    }

    protected void updateSoldTotalPrice() {
        totalPrice.setText(SoldProductViewModel.getTotalPrice(soldProductsModel) + " Ft");
    }

    protected List<ReceiptRecordView> findMatchingView(Collection<ReceiptRecordView> productsView, SoldProductViewModel row) {
        return productsView.stream()
                .filter(receiptRecordView -> SoldProductViewModel.isEquals(row, receiptRecordView))
                .collect(toList());
    }

    protected ReceiptRecordView decreaseRowInSoldProducts(SoldProductViewModel row, double amount) {
        List<ReceiptRecordView> matchingReceiptRecordView = findMatchingView(soldProductsView, row);
        if(matchingReceiptRecordView.size() == 0) return null;
        retailService.decreaseSoldQuantity(matchingReceiptRecordView.get(0), amount);
        decreaseClickedRow(row, amount);
        return matchingReceiptRecordView.get(0);
    }

    private void decreaseClickedRow(SoldProductViewModel row, double amount) {
        if(row.decreaseProductQuantity(amount)) {
            removeRowFromSoldProducts(row); // The whole product is paid, remove the row.
        }
        soldProductsTable.refresh();
    }

    protected ReceiptRecordView removeRowFromSoldProducts(final SoldProductViewModel row) {
        soldProductsModel.remove(row);
        soldProductsTable.setItems(soldProductsModel);
        List<ReceiptRecordView> matching = findMatchingView(soldProductsView, row);
        if(matching.size() == 0) return null;
        soldProductsView.remove(matching.get(0));
        return matching.get(0);
    }

    protected ReceiptRecordView increaseRowInSoldProducts(SoldProductViewModel row, double amount, boolean isSale) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(soldProductsView, row);
        retailService.increaseSoldQuantity(equivalentReceiptRecordView.get(0), amount, isSale);
        row.increaseProductQuantity(amount);
        return equivalentReceiptRecordView.get(0);
    }

    protected List<ReceiptRecordView> findEquivalentView(Collection<ReceiptRecordView> productsView, SoldProductViewModel row) {
        return productsView.stream()
                .filter(receiptRecordView -> SoldProductViewModel.isEquivalent(row, receiptRecordView))
                .collect(toList());
    }

    protected void addRowToSoldProducts(SoldProductViewModel row) {
        soldProductsModel.add(row);
        soldProductsModel.sort(Comparator.comparing(SoldProductViewModel::getProductId));
        soldProductsTable.setItems(soldProductsModel);
        soldProductsTable.refresh();
    }

    protected void getSoldProductsAndRefreshTable() {
        soldProductsView = getSoldProducts();
        refreshSoldProductsTable();
    }

    protected Collection<ReceiptRecordView> getSoldProducts() {
        receiptView = receiptService.getOpenReceipt(tableView.getNumber());
        return receiptView == null ? Collections.emptyList() : receiptView.getSoldProducts();
    }

    protected TableController getTableController() {
        if(tableView == null) {
            return null;
        }
        return tableConfigurationController.getTableController(tableView.getNumber());
    }
}
