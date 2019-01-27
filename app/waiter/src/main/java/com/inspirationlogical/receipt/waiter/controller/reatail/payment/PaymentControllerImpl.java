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
    TextField discountAbsoluteValue;

    @FXML
    ToggleButton discountPercent;
    @FXML
    TextField discountPercentValue;

    @FXML
    ToggleGroup discountTypeToggleGroup;

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
        updateTableSummary();
        resetToggleGroups();
        clearInputFields();
    }

    private void resetToggleGroups() {
        paymentMethodToggleGroup.selectToggle(paymentMethodCash);
        paymentTypeToggleGroup.selectToggle(null);
        discountTypeToggleGroup.selectToggle(null);
    }

    private void clearInputFields() {
        partialPaymentValue.clear();
        discountAbsoluteValue.clear();
        discountPercentValue.clear();
    }

    @FXML
    public void onPay(Event event) {
        logger.info("The pay button was pushed.");
        paymentViewState.handlePayment(isSoldProductsEmpty(), isPaidProductsEmpty());
    }

    private boolean isSoldProductsEmpty() {
        return soldProductsView.size() == 0;
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
        updatePaidTotalPrice();
        updateSoldTotalPrice();
        paidProductsTable.setItems(paidProductRowList);
        paidProductsTable.refresh();
    }

    private void updatePaidTotalPrice() {
        paidTotalPrice.setText(ProductRowModel.getTotalPrice(paidProductRowList) + " Ft");
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
            tableServicePay.payPartial(tableView, getPartialValue(), paymentParams);
            int totalPrice = ProductRowModel.getTotalPrice(soldProductsModel);
            getSoldProductsAndRefreshTable();
            int paidPartialPrice = totalPrice - ProductRowModel.getTotalPrice(soldProductsModel);
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
            totalPrice -= Integer.valueOf(discountAbsoluteValue.getText());
        } else if (paymentViewState.isDiscountPercent()) {
            double discountPercent = Double.valueOf(discountPercentValue.getText());
            totalPrice = (int) Math.round((double) totalPrice * ((100 - discountPercent) / 100));
        }
        return totalPrice;
    }

    @Override
    protected void soldProductsRowClickHandler(ProductRowModel row) {
        logger.info("The sold products table was clicked on the row: " + row.toString() + ", in payment view state: " + paymentViewState.toString());
        disableSoldProductsTableRowClickHandler();
        if (paymentViewState.isSelectivePayment()) {
            ReceiptRecordView clickedRecord = removeRowFromSoldProducts(row);
            if (clickedRecord == null) return;
            addRowToPaidProducts(row, clickedRecord);
        } else if (paymentViewState.isSinglePayment()) {
            singlePaymentRowClickHandler(row);
        } else if (paymentViewState.isPartialPayment()) {
            if (isPartiallyPayable(row)) {
                partialPaymentRowClickHandler(row);
            } else {
                ErrorMessage.showErrorMessage(rootPayment,
                        WaiterResources.WAITER.getString("PaymentView.ProductNotPartiallyPayable") + row.getProductName());
            }
        }
        enableSoldProductsTableRowClickHandler();
    }

    private void singlePaymentRowClickHandler(ProductRowModel row) {
        double amount = Math.min(Double.valueOf(row.getProductQuantity()), 1);
        updateSoldAndPaidProducts(row, amount);
    }

    private void updateSoldAndPaidProducts(ProductRowModel row, double amount) {
        ReceiptRecordView clickedRecord = decreaseRowInSoldProducts(row, amount);
        if (clickedRecord == null) {
            return;
        }
        increaseRowInPaidProducts(row, clickedRecord, amount);
        receiptRecordService.decreaseSoldQuantity(clickedRecord, amount);
    }

    private void partialPaymentRowClickHandler(ProductRowModel row) {
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

    private void addRowToPaidProducts(final ProductRowModel row, ReceiptRecordView toAdd) {
        paidProductViewList.add(toAdd);
        paidProductRowList.add(row);
        refreshPaidProductsTable();
    }

    private boolean isPartiallyPayable(ProductRowModel row) {
        List<ReceiptRecordView> matchingReceiptRecordView = findMatchingView(soldProductsView, row);
        return matchingReceiptRecordView.get(0).isPartiallyPayable();
    }

    private void increaseRowInPaidProducts(ProductRowModel row, ReceiptRecordView toAdd, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductViewList, row);
        if (equivalentReceiptRecordView.size() == 0) {
            cloneReceiptRecordAndAddToPaidProducts(row, toAdd, amount);
        } else {
            increaseReceiptRecordAndRowQuantity(amount, equivalentReceiptRecordView);
        }
        refreshPaidProductsTable();
    }

    private void cloneReceiptRecordAndAddToPaidProducts(ProductRowModel row, ReceiptRecordView toAdd, double amount) {
        ReceiptRecordView newRecord = receiptRecordService.cloneReceiptRecord(toAdd, amount);
        paidProductViewList.add(newRecord);
        paidProductRowList.add(createNewRow(row, newRecord, amount));
    }

    private void increaseReceiptRecordAndRowQuantity(double amount, List<ReceiptRecordView> equivalentReceiptRecordView) {
        ReceiptRecordView increasedRecord =
                receiptRecordService.increaseSoldQuantity(equivalentReceiptRecordView.get(0), amount, false);
        List<ProductRowModel> matchingRows =
                paidProductRowList.stream().filter(thisRow -> thisRow.isEqual(equivalentReceiptRecordView.get(0)))
                        .collect(toList());
        increaseRowQuantity(matchingRows.get(0), amount);
        paidProductViewList.remove(equivalentReceiptRecordView.get(0));
        paidProductViewList.add(increasedRecord);
    }

    private void increaseRowQuantity(ProductRowModel row, double amount) {
        row.increaseProductQuantity(amount);
        paidProductsTable.refresh();
    }

    private ProductRowModel createNewRow(ProductRowModel row, ReceiptRecordView newRecord, double amount) {
        ProductRowModel newRow = new ProductRowModel(row);
        newRow.setProductQuantity(String.valueOf(amount));
        newRow.setProductTotalPrice(String.valueOf((int) (Integer.valueOf(newRow.getProductUnitPrice()) * amount)));
        newRow.setProductId(newRecord.getId());
        return newRow;
    }

    void paidProductsRowClickHandler(ProductRowModel row) {
        logger.info("The paid products table was clicked for row: " + row);
        List<ProductRowModel> rowInSoldProducts = soldProductsModel.stream().filter(model -> model.getProductName().equals(row.getProductName()))
                .collect(toList());
        if (rowInSoldProducts.isEmpty()) {
            cloneReceiptRecordAndAddToSoldProducts(row);
            decreaseRowInPaidProducts(row, 1);
        } else {
            double amount = Double.parseDouble(row.getProductQuantity());
            amount = amount < 1 ? amount : 1;
            increaseRowInSoldProducts(rowInSoldProducts.get(0), amount, false);
            decreaseRowInPaidProducts(row, amount);
        }
        refreshPaidProductsTable();
        refreshSoldProductsTable();
    }

    private void decreaseRowInPaidProducts(ProductRowModel row, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductViewList, row);
        ReceiptRecordView decreasedRecord = receiptRecordService.decreaseSoldQuantity(equivalentReceiptRecordView.get(0), amount);
        removeRowFromPaidProducts(row);
        if (decreasedRecord != null) {
            addRowToPaidProducts(new ProductRowModel(decreasedRecord, getOrderDeliveredTime()), decreasedRecord);
        }
    }

    private void removeRowFromPaidProducts(final ProductRowModel row) {
        paidProductRowList.remove(row);
        paidProductsTable.setItems(paidProductRowList);
        List<ReceiptRecordView> matching = findMatchingView(paidProductViewList, row);
        paidProductViewList.remove(matching.get(0));
    }

    private void cloneReceiptRecordAndAddToSoldProducts(ProductRowModel row) {
        ReceiptRecordView recordInPaidProducts = findEquivalentView(paidProductViewList, row).get(0);
        ReceiptRecordView recordInSoldProducts = receiptRecordService.cloneReceiptRecord(recordInPaidProducts, 1);
        soldProductsView.add(recordInSoldProducts);
        addRowToSoldProducts(new ProductRowModel(recordInSoldProducts, getOrderDeliveredTime()));
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
        List<ReceiptRecordView> matchingGameFeeList = findMatchingView(soldProductsView, new ProductRowModel(gameFee, getOrderDeliveredTime()));
        if (matchingGameFeeList.isEmpty()) {
            soldProductsView.add(gameFee);
        } else {
            ReceiptRecordView matchingGameFee = matchingGameFeeList.get(0);
            soldProductsView.remove(matchingGameFee);
            soldProductsView.add(gameFee);
        }
    }
}
