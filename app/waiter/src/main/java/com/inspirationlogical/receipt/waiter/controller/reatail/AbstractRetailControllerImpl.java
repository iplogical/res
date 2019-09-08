package com.inspirationlogical.receipt.waiter.controller.reatail;

import com.inspirationlogical.receipt.corelib.frontend.controller.AbstractController;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptView;
import com.inspirationlogical.receipt.corelib.model.view.TableView;
import com.inspirationlogical.receipt.corelib.service.receipt.ReceiptService;
import com.inspirationlogical.receipt.corelib.service.receipt_record.ReceiptRecordService;
import com.inspirationlogical.receipt.corelib.service.table.TableServiceConfig;
import com.inspirationlogical.receipt.corelib.service.table.TableServicePay;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantFxmlView;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.controller.table.TableController;
import com.inspirationlogical.receipt.waiter.viewmodel.ProductRowModel;
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

import static java.util.stream.Collectors.toList;

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
    private javafx.scene.control.TableView<ProductRowModel> soldProductsTable;
    @FXML
    protected TableColumn<ProductRowModel, String> productName;
    @FXML
    private TableColumn<ProductRowModel, String> productQuantity;
    @FXML
    private TableColumn<ProductRowModel, String> productUnitPrice;
    @FXML
    private TableColumn<ProductRowModel, String> productTotalPrice;

    @Autowired
    protected ReceiptService receiptService;

    @Autowired
    protected ReceiptRecordService receiptRecordService;

    @Autowired
    private TableServiceConfig tableServiceConfig;

    @Autowired
    protected TableServicePay tableServicePay;

    @Autowired
    protected RestaurantController restaurantController;

    @Autowired
    protected TableConfigurationController tableConfigurationController;

    protected @Setter TableView tableView;

    protected ReceiptView receiptView;

    protected List<ReceiptRecordView> soldProductViewList;

    protected ObservableList<ProductRowModel> soldProductRowList = FXCollections.observableArrayList();

    @FXML
    public void onBackToRestaurantView(Event event) {
        logger.info("Back to restaurant view from table: " + tableView.toString());
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
        initColumn(productName, ProductRowModel::getProductName);
        initColumn(productQuantity, ProductRowModel::getProductQuantityWithRecent);
        initColumn(productUnitPrice, ProductRowModel::getProductUnitPrice);
        initColumn(productTotalPrice, ProductRowModel::getProductTotalPrice);
    }

    protected void enableSoldProductsTableRowClickHandler() {
        soldProductsTable.setRowFactory(tv -> {
            TableRow<ProductRowModel> row = new TableRow<>();
            row.setOnMouseClicked(enableRowClickHandler(row));
            return row;
        });
    }

    private EventHandler<MouseEvent> enableRowClickHandler(TableRow<ProductRowModel> row) {
        return event -> {
            if(event.getClickCount() == 1 && (!row.isEmpty())) {
                onSoldProductsRowClick(row.getItem());
            }
        };
    }

    protected void disableSoldProductsTableRowClickHandler() {
        soldProductsTable.setRowFactory(tv -> {
            TableRow<ProductRowModel> row = new TableRow<>();
            row.setOnMouseClicked(disableRowClickHandler(row));
            return row;
        });
    }

    private EventHandler<MouseEvent> disableRowClickHandler(TableRow<ProductRowModel> row) {
        return event -> {};
    }

    protected abstract void onSoldProductsRowClick(ProductRowModel row);

    protected void updateTableSummary() {
        tableName.setText(tableView.getName());
        tableNumber.setText(tableView.getNumber() + getGuestPerCapacity());
        note.setText(tableView.getNote());
    }

     private String getGuestPerCapacity() {
        return " (" + tableView.getGuestCount() + "/" + tableView.getCapacity() + ")";
    }

    protected ObservableList<ProductRowModel> convertReceiptRecordViewsToModel(Collection<ReceiptRecordView> soldProducts) {
        List<ProductRowModel> list = soldProducts.stream()
                .sorted(Comparator.comparing(this::getOldestCreated))
                .map(receiptRecordView -> new ProductRowModel(receiptRecordView, getFoodDeliveredTime(), getDrinkDeliveredTime()))
                .collect(toList());
        return FXCollections.observableArrayList(list);
    }

    private LocalDateTime getOldestCreated(ReceiptRecordView receiptRecord) {
        List<LocalDateTime> sortedCreatedList =  receiptRecord.getCreated();
        Collections.sort(sortedCreatedList);
        return sortedCreatedList.get(0);
    }

    protected LocalDateTime getFoodDeliveredTime() {
        return getTableController().getFoodDeliveryTime();
    }

    protected LocalDateTime getDrinkDeliveredTime() {
        return getTableController().getDrinkDeliveryTime();
    }

    protected void refreshSoldProductsTable() {
        soldProductRowList.clear();
        soldProductRowList.addAll(convertReceiptRecordViewsToModel(soldProductViewList));
        soldProductsTable.setItems(soldProductRowList);
        soldProductsTable.refresh();
        updateSoldTotalPrice();
    }

    private void updateSoldTotalPrice() {
        int totalPriceNoServiceFee = getTotalPrice();
        int totalServiceFee = receiptService.getTotalServiceFee(tableView.getNumber());
        totalPrice.setText(totalPriceNoServiceFee + " Ft" + " (" + (totalPriceNoServiceFee + totalServiceFee) + " Ft)");
    }

    protected int getTotalPrice() {
        return receiptService.getTotalPrice(tableView.getNumber());
    }

    protected ReceiptRecordView findMatchingSoldProduct(ProductRowModel row) {
        return soldProductViewList.stream().filter(row::isEqual).findFirst().get();
    }

    protected Optional<ReceiptRecordView> findMatchingSoldProductOptional(ProductRowModel row) {
        return soldProductViewList.stream().filter(row::isEqual).findFirst();
    }

    protected void increaseRowInSoldProducts(ReceiptRecordView equivalentReceiptRecordView, double amount, boolean isSale) {
        ReceiptRecordView increasedRecord = receiptRecordService.increaseSoldQuantity(equivalentReceiptRecordView, amount, isSale);
        soldProductViewList.remove(equivalentReceiptRecordView);
        soldProductViewList.add(increasedRecord);
    }

    protected ReceiptRecordView findEquivalentView(Collection<ReceiptRecordView> productsView, ProductRowModel row) {
        return productsView.stream().filter(row::isEquivalent).findFirst().get();
    }

    protected Optional<ReceiptRecordView> findEquivalentViewOptional(Collection<ReceiptRecordView> productsView, ProductRowModel row) {
        return productsView.stream().filter(row::isEquivalent).findFirst();
    }

    protected void getSoldProductsAndRefreshTable() {
        soldProductViewList = getSoldProducts();
        refreshSoldProductsTable();
    }

    private List<ReceiptRecordView> getSoldProducts() {
        receiptView = receiptService.getOpenReceipt(tableView.getNumber());
        return receiptView == null ? Collections.emptyList() : receiptView.getSoldProducts();
    }

    protected TableController getTableController() {
        return tableConfigurationController.getTableController(tableView.getNumber());
    }
}
