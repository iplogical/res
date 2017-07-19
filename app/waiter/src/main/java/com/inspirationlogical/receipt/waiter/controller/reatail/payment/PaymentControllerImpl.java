package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.FULL;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.PARTIAL;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.SELECTIVE;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.SINGLE;
import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.util.*;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.waiter.controller.reatail.sale.SaleController;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailControllerImpl;
import com.inspirationlogical.receipt.waiter.controller.restaurant.RestaurantController;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

@Singleton
public class PaymentControllerImpl extends AbstractRetailControllerImpl
        implements PaymentController {

    public static final String PAYMENT_VIEW_PATH = "/view/fxml/Payment.fxml";

    @FXML
    private BorderPane root;

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
    // TODO: input validation
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
    private Label payTotalPrice;
    @FXML
    private Label previousPartialPrice;

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

    @Inject
    private ViewLoader viewLoader;

    private SaleController saleController;

    PaymentViewState paymentViewState;

    private List<ReceiptRecordView> paidProductsView;

    private ObservableList<SoldProductViewModel> paidProductsModel;

    @Inject
    public PaymentControllerImpl(RetailService retailService,
                                 RestaurantService restaurantService,
                                 RestaurantController restaurantController,
                                 SaleController saleController) {
        super(restaurantService, retailService, restaurantController);
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
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeSoldProducts();
        new PaymentControllerInitializer(this).initialize();
        getSoldProductsAndUpdateTable();
        updateTableSummary();
    }

    @Override
    public void enterPaymentView() {
        if(!soldProductsTableInitialized) {
            return;
        }
        getSoldProductsAndUpdateTable();
        updateTableSummary();
        resetToggleGroups();
    }

    private void resetToggleGroups() {
        paymentMethodToggleGroup.selectToggle(paymentMethodCash);
        paymentTypeToggleGroup.selectToggle(null);
        discountTypeToggleGroup.selectToggle(null);
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
        getSoldProductsAndUpdateTable();
        discardPaidRecords();
        backToRestaurantView();
    }

    private void discardPaidRecords() {
        paidProductsModel = FXCollections.observableArrayList();
        paidProductsView = new ArrayList<>();
        previousPartialPrice.setText(payTotalPrice.getText());
        updatePaidProductsTable();
    }

    private void updatePaidProductsTable() {
        payTotalPrice.setText(SoldProductViewModel.getTotalPrice(paidProductsModel) + " Ft");
        updateSoldTotalPrice();
        paidProductsTable.setItems(paidProductsModel);
        paidProductsTable.refresh();
    }

    private void backToRestaurantView() {
        restaurantController.updateRestaurant();
        restaurantController.getTableController(tableView).updateNode();
        viewLoader.loadViewIntoScene(restaurantController);
    }
    @Override
    public void handleSelectivePayment(PaymentParams paymentParams) {
        retailService.paySelective(tableView, paidProductsView, paymentParams);
        discardPaidRecords();
        getSoldProductsAndUpdateTable();
    }

    @Override
    protected void soldProductsRowClickHandler(SoldProductViewModel row) {
        if(paymentViewState.isSelectivePayment()) {
            addRowToPaidProducts(row, removeRowFromSoldProducts(row));
        } else if(paymentViewState.isSinglePayment()) {
            if(isPartiallyPayable(row)) {
                double value = Math.min(Double.valueOf(row.getProductQuantity()), 1);
                increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, value), value);
            } else {
                increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, 1), 1);
            }
        } else if(paymentViewState.isPartialPayment()) {
            if(isPartiallyPayable(row)) {
                double amount = Double.valueOf(partialPaymentValue.getText());
                increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, amount), amount);
            }
        }
    }

    private void addRowToPaidProducts(final SoldProductViewModel row, ReceiptRecordView toAdd) {
        paidProductsView.add(toAdd);
        paidProductsModel.add(row);
        updatePaidProductsTable();
    }

    private boolean isPartiallyPayable(SoldProductViewModel row) {
        List<ReceiptRecordView> matchingReceiptRecordView = findMatchingView(soldProductsView, row);
        return matchingReceiptRecordView.get(0).isPartiallyPayable();
    }

    private void increaseRowInPaidProducts(SoldProductViewModel row, ReceiptRecordView toAdd, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductsView, row);
        if(equivalentReceiptRecordView.size() == 0) {
            ReceiptRecordView newRecord = retailService.cloneReceiptRecordView(tableView, toAdd, amount);
            paidProductsView.add(newRecord);
            paidProductsModel.add(createNewRow(row, newRecord, amount));
        } else {
            equivalentReceiptRecordView.get(0).increaseSoldQuantity(amount, false);
            List<SoldProductViewModel> matchingRows =
                    paidProductsModel.stream().filter(thisRow -> SoldProductViewModel.isEquals(thisRow, equivalentReceiptRecordView.get(0)))
                            .collect(toList());
            increaseRowQuantity(matchingRows.get(0), amount);
        }
        updatePaidProductsTable();
    }

    private SoldProductViewModel createNewRow(SoldProductViewModel row, ReceiptRecordView newRecord, double amount) {
        SoldProductViewModel newRow = new SoldProductViewModel(row);
        newRow.setProductQuantity(String.valueOf(amount));
        newRow.setProductTotalPrice(String.valueOf((int)(Integer.valueOf(newRow.getProductUnitPrice()) * amount)));
        newRow.setProductId(String.valueOf(newRecord.getId()));
        return newRow;
    }

    @FXML
    public void onBackToSaleView(Event event) {
        discardPaidRecords();
        retailService.mergeReceiptRecords(receiptView);
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
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
        updateTableSummary();
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
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
        updateTableSummary();
    }

    private void decreaseRowInPaidProducts(SoldProductViewModel row, ReceiptRecordView toAdd, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductsView, row);
        equivalentReceiptRecordView.get(0).decreaseSoldQuantity(amount);
        List<SoldProductViewModel> matchingRows =
                paidProductsModel.stream().filter(thisRow -> SoldProductViewModel.isEquals(thisRow, equivalentReceiptRecordView.get(0)))
                        .collect(toList());
        decreaseRowQuantity(matchingRows.get(0), amount);
    }

    private void increaseRowQuantity(SoldProductViewModel row, double amount) {
        paidProductsModel.remove(row);
        row.increaseProductQuantity(amount);
        paidProductsModel.add(row);
    }

    private void decreaseRowQuantity(SoldProductViewModel row, double amount) {
        paidProductsModel.remove(row);
        if(row.decreaseProductQuantity(amount)) {
            removeRowFromPaidProducts(row);
        }
        addRowToPaidProducts(row);
    }

    private void removeRowFromPaidProducts(final SoldProductViewModel row) {
        paidProductsModel.remove(row);
        paidProductsTable.setItems(paidProductsModel);
        List<ReceiptRecordView> matching = findMatchingView(paidProductsView, row);
        paidProductsView.remove(matching.get(0));
    }

    private void addRowToPaidProducts(SoldProductViewModel row) {
        paidProductsModel.add(row);
        paidProductsModel.sort(Comparator.comparing(SoldProductViewModel::getProductId));
        paidProductsTable.setItems(paidProductsModel);
        paidProductsTable.refresh();
    }

    void paidProductsRowClickHandler(SoldProductViewModel row) {
        List<SoldProductViewModel> rowInSoldProducts = soldProductsModel.stream().filter(model -> model.getProductName().equals(row.getProductName()))
                .collect(toList());
        if(rowInSoldProducts.isEmpty()) {
            ReceiptRecordView recordInPaidProducts = findEquivalentView(paidProductsView, row).get(0);
            ReceiptRecordView recordInSoldProducts = retailService.cloneReceiptRecordView(tableView, recordInPaidProducts, 1);
            addRowToSoldProducts(new SoldProductViewModel(recordInSoldProducts), recordInSoldProducts);
            decreaseRowInPaidProducts(row, recordInPaidProducts, 1);
        } else {
            decreaseRowInPaidProducts(row, increaseRowInSoldProducts(rowInSoldProducts.get(0), 1, false), 1);
        }
    }

    private void addRowToSoldProducts(final SoldProductViewModel row, ReceiptRecordView toAdd) {
        soldProductsView.add(toAdd);
        addRowToSoldProducts(row);
    }

    @Override
    public void onBackToRestaurantView(Event event) {
        discardPaidRecords();
        backToRestaurantView();
    }
}
