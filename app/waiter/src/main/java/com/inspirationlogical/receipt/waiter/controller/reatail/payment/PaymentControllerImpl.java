package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.params.VatCashierNumberModel;
import com.inspirationlogical.receipt.corelib.params.VatPriceModel;
import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
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
import java.util.*;

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
    Label paidPrice;

    @FXML
    Label previousPartialPrice;

    @FXML
    private Label vatDrinkPercentPrevious;
    @FXML
    private Label vatDrinkPricePrevious;
    @FXML
    private Label vatDrinkCashierNumberPrevious;
    @FXML
    private Label vatDrinkServiceFeePrevious;
    @FXML
    private Label vatDrinkServiceFeeCashierNumberPrevious;
    @FXML
    private Label vatDrinkTotalPricePrevious;

    @FXML
    private Label vatFoodPercentPrevious;
    @FXML
    private Label vatFoodPricePrevious;
    @FXML
    private Label vatFoodCashierNumberPrevious;
    @FXML
    private Label vatFoodServiceFeePrevious;
    @FXML
    private Label vatFoodServiceFeeCashierNumberPrevious;
    @FXML
    private Label vatFoodTotalPricePrevious;

    @FXML
    private Label vatDrinkPercent;
    @FXML
    private Label vatDrinkPrice;
    @FXML
    private Label vatDrinkCashierNumber;
    @FXML
    private Label vatDrinkServiceFee;
    @FXML
    private Label vatDrinkServiceFeeCashierNumber;
    @FXML
    private Label vatDrinkTotalPrice;

    @FXML
    private Label vatFoodPercent;
    @FXML
    private Label vatFoodPrice;
    @FXML
    private Label vatFoodCashierNumber;
    @FXML
    private Label vatFoodServiceFee;
    @FXML
    private Label vatFoodServiceFeeCashierNumber;
    @FXML
    private Label vatFoodTotalPrice;


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
        updateTotalPrices(paidProductViewList);
        updateTableSummary();
        resetToggleGroups();
        clearInputFields();
        updateCashierNumbers();
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

    private void updateCashierNumbers() {
        VatCashierNumberModel vatCashierNumberModel = tableServicePay.getVatCashierNumberModel();
        vatDrinkCashierNumber.setText(String.valueOf(vatCashierNumberModel.getVatDrinkCashierNumber()));
        vatDrinkCashierNumberPrevious.setText(String.valueOf(vatCashierNumberModel.getVatDrinkCashierNumber()));
        vatDrinkServiceFeeCashierNumber.setText(String.valueOf(vatCashierNumberModel.getVatDrinkServiceFeeCashierNumber()));
        vatDrinkServiceFeeCashierNumberPrevious.setText(String.valueOf(vatCashierNumberModel.getVatDrinkServiceFeeCashierNumber()));
        vatFoodCashierNumber.setText(String.valueOf(vatCashierNumberModel.getVatFoodCashierNumber()));
        vatFoodCashierNumberPrevious.setText(String.valueOf(vatCashierNumberModel.getVatFoodCashierNumber()));
        vatFoodServiceFeeCashierNumber.setText(String.valueOf(vatCashierNumberModel.getVatFoodServiceFeeCashierNumber()));
        vatFoodServiceFeeCashierNumberPrevious.setText(String.valueOf(vatCashierNumberModel.getVatFoodServiceFeeCashierNumber()));
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
        updateTotalPricesForFullPayment();
        tableView = tableServicePay.payTable(tableView.getNumber(), paymentParams);
        getTableController().setTableView(tableView);
        getSoldProductsAndRefreshTable();
        discardPaidRecords();
    }

    private void updateTotalPricesForFullPayment() {
        List<ReceiptRecordView> allProductsList = new ArrayList<>(soldProductViewList);
        allProductsList.addAll(paidProductViewList);
        updateTotalPrices(allProductsList);
    }

    private void discardPaidRecords() {
        paidProductRowList.clear();
        paidProductViewList.clear();
        refreshPaidProductsTable();
    }

    private void refreshPaidProductsTable() {
        paidProductRowList = convertReceiptRecordViewsToModel(paidProductViewList);
        paidProductsTable.setItems(paidProductRowList);
        paidProductsTable.refresh();
    }

    private void updateTotalPrices(List<ReceiptRecordView> productViewList) {
        int totalPricePaid = getTotalPrice(productViewList);
        int totalServiceFeePaid = getTotalServiceFee(productViewList);
        paidPrice.setText(totalPricePaid + " Ft");
        paidTotalPrice.setText("("+ (totalPricePaid + totalServiceFeePaid) + " Ft)");
        int totalPriceSold = getTotalPrice() - totalPricePaid;
        int totalServiceFeeSold = receiptService.getTotalServiceFee(tableView.getNumber()) - totalServiceFeePaid;
        totalPrice.setText(totalPriceSold + " Ft" + " (" + (totalPriceSold + totalServiceFeeSold) + " Ft)");
        updatePriceLabels(productViewList);
    }

    private void updatePriceLabels(List<ReceiptRecordView> productViewList) {
        Map<VATName, VatPriceModel> vatPriceModelMap = tableServicePay.getVatPriceModelMap(productViewList);
        updateDrinkPriceLabels(vatPriceModelMap.get(VATName.NORMAL));
        updateFoodPriceLabels(vatPriceModelMap.get(VATName.GREATLY_REDUCED));
    }

    private void updateDrinkPriceLabels(VatPriceModel vatPriceModel) {
        vatDrinkPercent.setText(vatPriceModel.getVatPercent() + " % :");
        vatDrinkPrice.setText(vatPriceModel.getPrice() + " Ft");
        vatDrinkServiceFee.setText(vatPriceModel.getServiceFee() + " Ft");
        vatDrinkTotalPrice.setText(vatPriceModel.getTotalPrice() + " Ft");
    }

    private void updateFoodPriceLabels(VatPriceModel vatPriceModel) {
        vatFoodPercent.setText(vatPriceModel.getVatPercent() + " % :");
        vatFoodPrice.setText(vatPriceModel.getPrice() + " Ft");
        vatFoodServiceFee.setText(vatPriceModel.getServiceFee() + " Ft");
        vatFoodTotalPrice.setText(vatPriceModel.getTotalPrice() + " Ft");
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
        updatePreviousPartialPriceValues();
        discardPaidRecords();
        getSoldProductsAndRefreshTable();
    }

    private void updatePreviousPartialPriceValues() {
        int totalPrice = paymentViewState.isServiceFeeState() ? getTotalPriceWithServiceFee() : Integer.parseInt(paidPrice.getText().split(" ")[0]);
        previousPartialPrice.setText(applyDiscountOnTotalPrice(totalPrice) + " Ft");
        updateDrinkPricePreviousLabels();
        updateFoodPricePreviousLabels();
    }

    private int getTotalPriceWithServiceFee() {
        return Integer.parseInt(paidTotalPrice.getText().split(" ")[0]
                .replaceAll("\\(", "")
                .replaceAll("\\)", ""));
    }

    private int applyDiscountOnTotalPrice(int totalPrice) {

        if (paymentViewState.isDiscountAbsolute()) {
            totalPrice -= Integer.parseInt(discountValue.getText());
        } else if (paymentViewState.isDiscountPercent()) {
            double discountPercent = Double.parseDouble(discountValue.getText());
            totalPrice = (int) Math.round((double) totalPrice * ((100 - discountPercent) / 100));
        }
        return totalPrice;
    }

    private void updateDrinkPricePreviousLabels() {
        vatDrinkPercentPrevious.setText(vatDrinkPercent.getText());
        vatDrinkPricePrevious.setText(vatDrinkPrice.getText());
        vatDrinkServiceFeePrevious.setText(vatDrinkServiceFee.getText());
        vatDrinkTotalPricePrevious.setText(vatDrinkTotalPrice.getText());
    }

    private void updateFoodPricePreviousLabels() {
        vatFoodPercentPrevious.setText(vatFoodPercent.getText());
        vatFoodPricePrevious.setText(vatFoodPrice.getText());
        vatFoodServiceFeePrevious.setText(vatFoodServiceFee.getText());
        vatFoodTotalPricePrevious.setText(vatFoodTotalPrice.getText());
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
            NotificationMessage.showErrorMessage(getRootNode(), WaiterResources.WAITER.getString("PaymentView.PartialPayNumberErrorRange"));
        }
    }

    private double getPartialValue() throws NumberFormatException {
        double partialValue = Double.valueOf(partialPaymentValue.getText());
        if (partialValue > 1.00 || partialValue < 0.01) {
            throw new NumberFormatException();
        }
        return partialValue;
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
                NotificationMessage.showErrorMessage(rootPayment,
                        WaiterResources.WAITER.getString("PaymentView.ProductNotPartiallyPayable") + row.getProductName());
            }
        }
        refreshSoldProductsTable();
        refreshPaidProductsTable();
        updateTotalPrices(paidProductViewList);
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
                NotificationMessage.showErrorMessage(rootPayment,
                        WaiterResources.WAITER.getString("PaymentView.PartialPayBiggerAmountError"));
                return;
            }
            updateSoldAndPaidProducts(row, amount);
        } catch (NumberFormatException e) {
            NotificationMessage.showErrorMessage(rootPayment,
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
        updateTotalPrices(paidProductViewList);
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
        if (receiptView != null) {
            receiptService.mergeReceiptRecords(receiptView);
        }
        backToRestaurantView();
    }

    @FXML
    public void onBackToSaleView(Event event) {
        logger.info("Going to sale view.");
        discardPaidRecords();
        if (receiptView != null) {
            receiptService.mergeReceiptRecords(receiptView);
            enterSaleView();
        } else {
            backToRestaurantView();
        }
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
