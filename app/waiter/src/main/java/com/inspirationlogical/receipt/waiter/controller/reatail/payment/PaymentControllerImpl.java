package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.controller.table.TableConfigurationController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static java.util.stream.Collectors.toList;

@Singleton
public class PaymentControllerImpl extends AbstractRetailControllerImpl
        implements PaymentController {

    public static final String PAYMENT_VIEW_PATH = "/view/fxml/Payment.fxml";

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
    javafx.scene.control.TableView<SoldProductViewModel> paidProductsTable;
    @FXML
    TableColumn payProductName;
    @FXML
    TableColumn payProductQuantity;
    @FXML
    TableColumn payProductUnitPrice;
    @FXML
    TableColumn payProductTotalPrice;

    private SaleController saleController;

    PaymentViewState paymentViewState;

    private List<ReceiptRecordView> paidProductsView;

    private ObservableList<SoldProductViewModel> paidProductsModel;

    @Inject
    public PaymentControllerImpl(RetailService retailService,
                                 RestaurantService restaurantService,
                                 RestaurantController restaurantController,
                                 SaleController saleController,
                                 TableConfigurationController tableConfigurationController) {
        super(restaurantService, retailService, restaurantController, tableConfigurationController);
        this.saleController = saleController;
        paymentViewState = new PaymentViewState(this);
        paidProductsModel = FXCollections.observableArrayList();
        paidProductsView = new ArrayList<>();
    }

    @Override
    public String getViewPath() {
        return PAYMENT_VIEW_PATH;
    }

    @Override
    public Node getRootNode() {
        return rootPayment;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        paymentViewState.handlePayment(isSoldProductsEmpty(), isPaidProductsEmpty());
    }

    private boolean isSoldProductsEmpty() {
        return soldProductsView.size() == 0;
    }

    private boolean isPaidProductsEmpty() {
        return paidProductsView.size() == 0;
    }

    @Override
    public void handleFullPayment(PaymentParams paymentParams) {
        retailService.payTable(tableView, paymentParams);
        getSoldProductsAndRefreshTable();
        discardPaidRecords();
        clearPreviousPartialPrice();
        backToRestaurantView();
    }

    private void discardPaidRecords() {
        paidProductsModel.clear();
        paidProductsView.clear();
        refreshPaidProductsTable();
    }

    private void clearPreviousPartialPrice() {
        previousPartialPrice.setText("0 Ft");
    }

    private void refreshPaidProductsTable() {
        updatePaidTotalPrice();
        updateSoldTotalPrice();
        paidProductsTable.setItems(paidProductsModel);
        paidProductsTable.refresh();
    }

    private void updatePaidTotalPrice() {
        paidTotalPrice.setText(SoldProductViewModel.getTotalPrice(paidProductsModel) + " Ft");
    }

    @Override
    public void handleSelectivePayment(PaymentParams paymentParams) {
        retailService.paySelective(tableView, paidProductsView, paymentParams);
        updatePreviousPartialPrice();
        discardPaidRecords();
        getSoldProductsAndRefreshTable();
    }

    @Override
    public void handlePartialPayment(PaymentParams paymentParams) {
        try {
            retailService.payPartial(tableView, getPartialValue(), paymentParams);
            int totalPrice = SoldProductViewModel.getTotalPrice(soldProductsModel);
            getSoldProductsAndRefreshTable();
            int paidPartialPrice = totalPrice - SoldProductViewModel.getTotalPrice(soldProductsModel);
            previousPartialPrice.setText(applyDiscountOnTotalPrice(paidPartialPrice) + " Ft");
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(getRootNode(), WaiterResources.WAITER.getString("PaymentView.PartialPayNumberErrorRange"));
        }
    }

    private double getPartialValue() throws NumberFormatException {
        double partialValue = Double.valueOf(partialPaymentValue.getText());
        if(partialValue > 1.00 || partialValue < 0.01) {
            throw new NumberFormatException();
        }
        return partialValue;
    }

    private void updatePreviousPartialPrice() {
        int totalPrice = Integer.valueOf(paidTotalPrice.getText().split(" ")[0]);
        previousPartialPrice.setText(applyDiscountOnTotalPrice(totalPrice) + " Ft");
    }

    private int applyDiscountOnTotalPrice(int totalPrice) {
        if(paymentViewState.isDiscountAbsolute()) {
            totalPrice -= Integer.valueOf(discountAbsoluteValue.getText());
        } else if(paymentViewState.isDiscountPercent()) {
            double discountPercent = Double.valueOf(discountPercentValue.getText());
            totalPrice = (int)Math.round((double)totalPrice * ((100 - discountPercent) / 100));
        }
        return totalPrice;
    }

    @Override
    protected void soldProductsRowClickHandler(SoldProductViewModel row) {
        if(paymentViewState.isSelectivePayment()) {
            addRowToPaidProducts(row, removeRowFromSoldProducts(row));
        } else if(paymentViewState.isSinglePayment()) {
            singlePaymentRowClickHandler(row);
        } else if(paymentViewState.isPartialPayment()) {
            if(isPartiallyPayable(row)) {
                partialPaymentRowClickHandler(row);
            }
        }
    }

    private void singlePaymentRowClickHandler(SoldProductViewModel row) {
            double value = Math.min(Double.valueOf(row.getProductQuantity()), 1);
            increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, value), value);
    }

    private void partialPaymentRowClickHandler(SoldProductViewModel row) {
        try {
            double amount = Double.valueOf(partialPaymentValue.getText());
            increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, amount), amount);
        } catch (NumberFormatException e) {
            ErrorMessage.showErrorMessage(rootPayment,
                    WaiterResources.WAITER.getString("PaymentView.PartialPayNumberError"));
        }
    }

    private void addRowToPaidProducts(final SoldProductViewModel row, ReceiptRecordView toAdd) {
        paidProductsView.add(toAdd);
        paidProductsModel.add(row);
        refreshPaidProductsTable();
    }

    private boolean isPartiallyPayable(SoldProductViewModel row) {
        List<ReceiptRecordView> matchingReceiptRecordView = findMatchingView(soldProductsView, row);
        return matchingReceiptRecordView.get(0).isPartiallyPayable();
    }

    private void increaseRowInPaidProducts(SoldProductViewModel row, ReceiptRecordView toAdd, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductsView, row);
        if(equivalentReceiptRecordView.size() == 0) {
            cloneReceiptRecordAndAddToPaidProducts(row, toAdd, amount);
        } else {
            increaseReceiptRecordAndRowQuantity(amount, equivalentReceiptRecordView);
        }
        refreshPaidProductsTable();
    }

    private void cloneReceiptRecordAndAddToPaidProducts(SoldProductViewModel row, ReceiptRecordView toAdd, double amount) {
        ReceiptRecordView newRecord = retailService.cloneReceiptRecordView(tableView, toAdd, amount);
        paidProductsView.add(newRecord);
        paidProductsModel.add(createNewRow(row, newRecord, amount));
    }

    private void increaseReceiptRecordAndRowQuantity(double amount, List<ReceiptRecordView> equivalentReceiptRecordView) {
        equivalentReceiptRecordView.get(0).increaseSoldQuantity(amount, false);
        List<SoldProductViewModel> matchingRows =
                paidProductsModel.stream().filter(thisRow -> SoldProductViewModel.isEquals(thisRow, equivalentReceiptRecordView.get(0)))
                        .collect(toList());
        increaseRowQuantity(matchingRows.get(0), amount);
    }

    private void increaseRowQuantity(SoldProductViewModel row, double amount) {
        row.increaseProductQuantity(amount);
        paidProductsTable.refresh();
    }

    private SoldProductViewModel createNewRow(SoldProductViewModel row, ReceiptRecordView newRecord, double amount) {
        SoldProductViewModel newRow = new SoldProductViewModel(row);
        newRow.setProductQuantity(String.valueOf(amount));
        newRow.setProductTotalPrice(String.valueOf((int)(Integer.valueOf(newRow.getProductUnitPrice()) * amount)));
        newRow.setProductId(String.valueOf(newRecord.getId()));
        return newRow;
    }

    void paidProductsRowClickHandler(SoldProductViewModel row) {
        List<SoldProductViewModel> rowInSoldProducts = soldProductsModel.stream().filter(model -> model.getProductName().equals(row.getProductName()))
                .collect(toList());
        if(rowInSoldProducts.isEmpty()) {
            ReceiptRecordView recordInPaidProducts = cloneReceiptRecordAndAddToSoldProducts(row);
            decreaseRowInPaidProducts(row, recordInPaidProducts, 1);
        } else {
            decreaseRowInPaidProducts(row, increaseRowInSoldProducts(rowInSoldProducts.get(0), 1, false), 1);
        }
        refreshPaidProductsTable();
        refreshSoldProductsTable();
    }

    private void decreaseRowInPaidProducts(SoldProductViewModel row, ReceiptRecordView toAdd, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductsView, row);
        equivalentReceiptRecordView.get(0).decreaseSoldQuantity(amount);
        List<SoldProductViewModel> matchingRows =
                paidProductsModel.stream().filter(thisRow -> SoldProductViewModel.isEquals(thisRow, equivalentReceiptRecordView.get(0)))
                        .collect(toList());
        decreaseRowQuantity(matchingRows.get(0), amount);
    }

    private void decreaseRowQuantity(SoldProductViewModel row, double amount) {
        if(row.decreaseProductQuantity(amount)) {
            removeRowFromPaidProducts(row);
        }
    }

    private void removeRowFromPaidProducts(final SoldProductViewModel row) {
        paidProductsModel.remove(row);
        paidProductsTable.setItems(paidProductsModel);
        List<ReceiptRecordView> matching = findMatchingView(paidProductsView, row);
        paidProductsView.remove(matching.get(0));
    }

    private ReceiptRecordView cloneReceiptRecordAndAddToSoldProducts(SoldProductViewModel row) {
        ReceiptRecordView recordInPaidProducts = findEquivalentView(paidProductsView, row).get(0);
        ReceiptRecordView recordInSoldProducts = retailService.cloneReceiptRecordView(tableView, recordInPaidProducts, 1);
        soldProductsView.add(recordInSoldProducts);
        addRowToSoldProducts(new SoldProductViewModel(recordInSoldProducts));
        return recordInPaidProducts;
    }

    @Override
    public void onBackToRestaurantView(Event event) {
        discardPaidRecords();
        retailService.mergeReceiptRecords(receiptView);
        backToRestaurantView();
    }

    @FXML
    public void onBackToSaleView(Event event) {
        discardPaidRecords();
        retailService.mergeReceiptRecords(receiptView);
        enterSaleView();
    }

    private void enterSaleView() {
        saleController.enterSaleView();
        viewLoader.loadViewIntoScene(saleController);
    }

    @FXML
    public void onAutoGameFee(Event event) {
        ReceiptRecordView gameFee = handleAutomaticGameFee();
        if(gameFee == null) return;
        if(findMatchingView(soldProductsView, new SoldProductViewModel(gameFee)).size() == 0) {
            soldProductsView.add(gameFee);
        }
        refreshSoldProductsTable();
    }

    private ReceiptRecordView handleAutomaticGameFee() {
        //TODO: Make the 2000 configurable from the manager terminal.
        int guestCount = tableView.getGuestCount();
        int price = (int)receiptView.getTotalPrice();
        int requiredGameFee = (((guestCount+1) * 2000 - (price+1)) / 2000);
        if(requiredGameFee > 0) {
            return retailService.sellGameFee(tableView, requiredGameFee);
        }
        return null;
    }

    @FXML
    public void onManualGameFee(Event event) {
        ReceiptRecordView gameFee = retailService.sellGameFee(tableView, 1);
        if(findMatchingView(soldProductsView, new SoldProductViewModel(gameFee)).size() == 0) {
            soldProductsView.add(gameFee);
        }
        refreshSoldProductsTable();
    }
}
