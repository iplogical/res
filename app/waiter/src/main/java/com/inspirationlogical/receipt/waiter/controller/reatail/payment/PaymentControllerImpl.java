package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleFxmlView;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import com.inspirationlogical.receipt.waiter.viewmodel.ProductRowModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

@Component
public class PaymentControllerImpl extends AbstractRetailControllerImpl
        implements PaymentController {

    final private static Logger logger = LoggerFactory.getLogger(PaymentControllerImpl.class);

    @FXML
    private BorderPane rootPayment;

    @FXML
    RadioButton paymentMethodCash;
    @FXML
    RadioButton paymentMethodCreditCard;
    @FXML
    RadioButton paymentMethodCoupon;
    @FXML
    ToggleGroup paymentMethodToggleGroup;

    @FXML
    RadioButton doublePrint;

    @FXML
    RadioButton serviceFee;

    @FXML
    ToggleButton selectivePayment;
    @FXML
    ToggleButton singlePayment;
    @FXML
    ToggleButton partialPayment;
    @FXML
    ToggleGroup paymentTypeToggleGroup;

    @FXML
    private TextField partialPaymentValue;

    @FXML
    private Button automaticGameFee;

    @FXML
    private Button manualGameFee;

    @FXML
    ToggleButton discountAbsolute;
    @FXML
    ToggleButton discountPercent;
    @FXML
    ToggleGroup discountTypeToggleGroup;

    @FXML
    TextField discountValue;


    @FXML
    Label paidTotalPrice;

    @FXML
    Label previousPartialPrice;

    @FXML
    Label liveTime;

    @FXML
    javafx.scene.control.TableView<ProductRowModel> paidProductsTable;
    @FXML
    TableColumn payProductName;
    @FXML
    TableColumn payProductQuantity;
    @FXML
    TableColumn payProductUnitPrice;
    @FXML
    TableColumn payProductTotalPrice;

    @Autowired
    private SaleController saleController;

    PaymentViewState paymentViewState;

    private List<ReceiptRecordView> paidProductViewList;

    private ObservableList<ProductRowModel> paidProductRowList;

    @Override
    public Node getRootNode() {
        return rootPayment;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paymentViewState = new PaymentViewState(this);
        paidProductRowList = FXCollections.observableArrayList();
        paidProductViewList = new ArrayList<>();
        initializeSoldProducts();
        new PaymentControllerInitializer(this).initialize();
    }

    @Override
    public void enterPaymentView() {
        getSoldProductsAndRefreshTable();
        updateTotalPrices();
        updateTableSummary();
        resetToggleGroups();
        clearInputFields();
        serviceFee.setSelected(true);
    }

    private void resetToggleGroups() {
        paymentMethodToggleGroup.selectToggle(paymentMethodCash);
        paymentTypeToggleGroup.selectToggle(null);
        discountTypeToggleGroup.selectToggle(null);
    }

    private void clearInputFields() {
        partialPaymentValue.clear();
        discountValue.clear();
    }

    @FXML
    public void onPay(Event event) {
        logger.info("The pay button was pushed.");
        paymentViewState.handlePayment(isSoldProductsEmpty(), isPaidProductsEmpty());
    }

    private boolean isSoldProductsEmpty() {
        return soldProductViewList.size() == 0;
    }

    private boolean isPaidProductsEmpty() {
        return paidProductViewList.size() == 0;
    }

    @Override
    public void handleFullPayment(PaymentParams paymentParams) {
        logger.info("Handling full payment with paymentParams: " + paymentParams.toString());
        tableView = tableServicePay.payTable(tableView.getNumber(), paymentParams);
        getTableController().setTableView(tableView);
        getSoldProductsAndRefreshTable();
        discardPaidRecords();
        clearPreviousPartialPrice();
        backToRestaurantView();
    }

    private void discardPaidRecords() {
        paidProductRowList.clear();
        paidProductViewList.clear();
        refreshPaidProductsTable();
    }

    private void clearPreviousPartialPrice() {
        previousPartialPrice.setText("0 Ft");
    }

    private void refreshPaidProductsTable() {
        paidProductRowList = convertReceiptRecordViewsToModel(paidProductViewList);
        paidProductsTable.setItems(paidProductRowList);
        paidProductsTable.refresh();
        updateTotalPrices();
    }

    private void updateTotalPrices() {
        int totalPricePaid = getTotalPrice(paidProductViewList);
        int totalServiceFeePaid = getTotalServiceFee(paidProductViewList);
        paidTotalPrice.setText(totalPricePaid + " Ft" + " ("+ (totalPricePaid + totalServiceFeePaid) + " Ft)");
        int totalPriceSold = getTotalPrice() - totalPricePaid;
        int totalServiceFeeSold = receiptService.getTotalServiceFee(tableView.getNumber()) - totalServiceFeePaid;
        totalPrice.setText(totalPriceSold + " Ft" + " (" + (totalPriceSold + totalServiceFeeSold) + " Ft)");
    }

    private int getTotalPrice(List<ReceiptRecordView> recordViewList) {
        return tableServicePay.getTotalPrice(recordViewList);
    }

    private int getTotalServiceFee(List<ReceiptRecordView> recordViewList) {
        return tableServicePay.getTotalServiceFee(recordViewList);
    }

    @Override
    public void handleSelectivePayment(PaymentParams paymentParams) {
        logger.info("Handling selective payment with paymentParams: " + paymentParams.toString());
        tableServicePay.paySelective(tableView, paidProductViewList, paymentParams);
        updatePreviousPartialPrice();
        discardPaidRecords();
        getSoldProductsAndRefreshTable();
    }

    @Override
    public void handlePartialPayment(PaymentParams paymentParams) {
        try {
            logger.info("Handling selective payment with paymentParams: " + paymentParams.toString());
            int totalPrice = getTotalPrice();
            tableServicePay.payPartial(tableView, getPartialValue(), paymentParams);
            getSoldProductsAndRefreshTable();
            int paidPartialPrice = totalPrice - getTotalPrice();
            previousPartialPrice.setText(applyDiscountOnTotalPrice(paidPartialPrice) + " Ft");
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(getRootNode(), WaiterResources.WAITER.getString("PaymentView.PartialPayNumberErrorRange"));
        }
    }

    private double getPartialValue() throws NumberFormatException {
        double partialValue = Double.valueOf(partialPaymentValue.getText());
        if (partialValue > 1.00 || partialValue < 0.01) {
            throw new NumberFormatException();
        }
        return partialValue;
    }

    private void updatePreviousPartialPrice() {
        int totalPrice = Integer.valueOf(paidTotalPrice.getText().split(" ")[0]);
        previousPartialPrice.setText(applyDiscountOnTotalPrice(totalPrice) + " Ft");
    }

    private int applyDiscountOnTotalPrice(int totalPrice) {
        if (paymentViewState.isDiscountAbsolute()) {
            totalPrice -= Integer.valueOf(discountValue.getText());
        } else if (paymentViewState.isDiscountPercent()) {
            double discountPercent = Double.valueOf(discountValue.getText());
            totalPrice = (int) Math.round((double) totalPrice * ((100 - discountPercent) / 100));
        }
        return totalPrice;
    }

    @Override
    protected void onSoldProductsRowClick(ProductRowModel row) {
        logger.info("The sold products table was clicked on the row: " + row.toString() + ", in payment view state: " + paymentViewState.toString());
        disableSoldProductsTableRowClickHandler();
        if (paymentViewState.isSelectivePayment()) {
            onSelectivePaymentRowClick(row);
        } else if (paymentViewState.isSinglePayment()) {
            onSinglePaymentRowClick(row);
        } else if (paymentViewState.isPartialPayment()) {
            if (isPartiallyPayable(row)) {
                onPartialPaymentRowClick(row);
            } else {
                ErrorMessage.showErrorMessage(rootPayment,
                        WaiterResources.WAITER.getString("PaymentView.ProductNotPartiallyPayable") + row.getProductName());
            }
        }
        refreshSoldProductsTable();
        refreshPaidProductsTable();
        enableSoldProductsTableRowClickHandler();
    }

    private void onSelectivePaymentRowClick(ProductRowModel row) {
        ReceiptRecordView clickedRecord = findMatchingSoldProduct(row);
        soldProductViewList.remove(clickedRecord);
        paidProductViewList.add(clickedRecord);
    }

    private void onSinglePaymentRowClick(ProductRowModel row) {
        double amount = Math.min(Double.valueOf(row.getProductQuantity()), 1);
        updateSoldAndPaidProducts(row, amount);
    }

    private void updateSoldAndPaidProducts(ProductRowModel row, double amount) {
        ReceiptRecordView clickedRecord = findMatchingSoldProduct(row);
        increaseRowInPaidProducts(row, clickedRecord, amount);
        decreaseRowInSoldProducts(amount, clickedRecord);
    }

    private void decreaseRowInSoldProducts(double amount, ReceiptRecordView clickedRecord) {
        ReceiptRecordView decreasedRecord = receiptRecordService.decreaseSoldQuantity(clickedRecord, amount);
        soldProductViewList.remove(clickedRecord);
        if(decreasedRecord != null) {
            soldProductViewList.add(decreasedRecord);
        }
    }

    private void increaseRowInPaidProducts(ProductRowModel row, ReceiptRecordView toAdd, double amount) {
        Optional<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentViewOptional(paidProductViewList, row);
        if (!equivalentReceiptRecordView.isPresent()) {
            cloneReceiptRecord(row, toAdd, amount);
        } else {
            increaseReceiptRecord(amount, equivalentReceiptRecordView.get());
        }
    }

    private void cloneReceiptRecord(ProductRowModel row, ReceiptRecordView toAdd, double amount) {
        ReceiptRecordView newRecord = receiptRecordService.cloneReceiptRecord(toAdd, amount);
        paidProductViewList.add(newRecord);
    }

    private void increaseReceiptRecord(double amount, ReceiptRecordView equivalentReceiptRecordView) {
        ReceiptRecordView increasedRecord =
        receiptRecordService.increaseSoldQuantity(equivalentReceiptRecordView, amount, false);
        paidProductViewList.remove(equivalentReceiptRecordView);
        paidProductViewList.add(increasedRecord);
    }

    private boolean isPartiallyPayable(ProductRowModel row) {
        ReceiptRecordView matchingReceiptRecordView = findMatchingSoldProduct(row);
        return matchingReceiptRecordView.isPartiallyPayable();
    }

    private void onPartialPaymentRowClick(ProductRowModel row) {
        try {
            double amount = Double.valueOf(partialPaymentValue.getText());
            if(amount > Double.parseDouble(row.getProductQuantity())) {
                ErrorMessage.showErrorMessage(rootPayment,
                        WaiterResources.WAITER.getString("PaymentView.PartialPayBiggerAmountError"));
                return;
            }
            updateSoldAndPaidProducts(row, amount);
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(rootPayment,
                    WaiterResources.WAITER.getString("PaymentView.PartialPayNumberError"));
        }
    }

    void onPaidProductsRowClicked(ProductRowModel row) {
        logger.info("The paid products table was clicked for row: " + row);
        List<ProductRowModel> rowInSoldProducts = soldProductRowList.stream().filter(model -> model.getProductName().equals(row.getProductName()))
                .collect(toList());
        if (rowInSoldProducts.isEmpty()) {
            cloneReceiptRecordAndAddToSoldProducts(row);
            decreaseRowInPaidProducts(row, 1);
        } else {
            double amount = Double.parseDouble(row.getProductQuantity());
            amount = amount < 1 ? amount : 1;
            ReceiptRecordView equivalentReceiptRecordView = findEquivalentView(soldProductViewList, row);
            increaseRowInSoldProducts(equivalentReceiptRecordView, amount, false);
            decreaseRowInPaidProducts(row, amount);
        }
        refreshSoldProductsTable();
        refreshPaidProductsTable();
    }

    private void decreaseRowInPaidProducts(ProductRowModel row, double amount) {
        ReceiptRecordView equivalentReceiptRecordView = findEquivalentView(paidProductViewList, row);
        ReceiptRecordView decreasedRecord = receiptRecordService.decreaseSoldQuantity(equivalentReceiptRecordView, amount);
        paidProductViewList.remove(equivalentReceiptRecordView);
        if (decreasedRecord != null) {
            paidProductViewList.add(decreasedRecord);
        }
    }

    private void cloneReceiptRecordAndAddToSoldProducts(ProductRowModel row) {
        ReceiptRecordView recordInPaidProducts = findEquivalentView(paidProductViewList, row);
        ReceiptRecordView recordInSoldProducts = receiptRecordService.cloneReceiptRecord(recordInPaidProducts, 1);
        soldProductViewList.add(recordInSoldProducts);
    }

    @Override
    public void onBackToRestaurantView(Event event) {
        logger.info("Going to restaurant view.");
        discardPaidRecords();
        receiptService.mergeReceiptRecords(receiptView);
        backToRestaurantView();
    }

    @FXML
    public void onBackToSaleView(Event event) {
        logger.info("Going to sale view.");
        discardPaidRecords();
        receiptService.mergeReceiptRecords(receiptView);
        enterSaleView();
    }

    private void enterSaleView() {
        saleController.setTableView(tableView);
        saleController.enterSaleView();
        WaiterApp.showView(SaleFxmlView.class);
    }

    @FXML
    public void onAutoGameFee(Event event) {
        logger.info("The auto game fee button was clicked");
        ReceiptRecordView gameFee = handleAutomaticGameFee();
        if (gameFee == null) {
            return;
        }
        updateSoldProductsViewWithGameFee(gameFee);
        refreshSoldProductsTable();
    }

    private ReceiptRecordView handleAutomaticGameFee() {
        //TODO: Make the 2000 configurable from the manager terminal.
        int guestCount = tableView.getGuestCount();
        int price = (int) receiptView.getTotalPrice();
        int requiredGameFee = (((guestCount + 1) * 2000 - (price + 1)) / 2000);
        if (requiredGameFee > 0) {
            receiptService.sellGameFee(tableView, requiredGameFee);
            return receiptService.getLatestGameFee(tableView);
        }
        return null;
    }

    @FXML
    public void onManualGameFee(Event event) {
        logger.info("The manual game fee button was clicked");
        receiptService.sellGameFee(tableView, 1);
        ReceiptRecordView gameFee = receiptService.getLatestGameFee(tableView);
        updateSoldProductsViewWithGameFee(gameFee);
        refreshSoldProductsTable();
    }

    private void updateSoldProductsViewWithGameFee(ReceiptRecordView gameFee) {
        Optional<ReceiptRecordView> matchingGameFeeOptional = findMatchingSoldProductOptional(new ProductRowModel(gameFee, getFoodDeliveredTime(), getDrinkDeliveredTime()));
        if (matchingGameFeeOptional.isPresent()) {
            ReceiptRecordView matchingGameFee = matchingGameFeeOptional.get();
            soldProductViewList.remove(matchingGameFee);
        }
        soldProductViewList.add(gameFee);
        receiptView.updateTotalPrice();
    }
}
