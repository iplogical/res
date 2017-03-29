package com.inspirationlogical.receipt.waiter.controller;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class PaymentControllerImpl extends AbstractRetailControllerImpl
        implements PaymentController {

    public static final String PAYMENT_VIEW_PATH = "/view/fxml/PaymentView.fxml";

    @FXML
    private BorderPane root;

    @FXML
    RadioButton paymentMethodCash;
    @FXML
    RadioButton paymentMethodCreditCard;
    @FXML
    RadioButton paymentMethodCoupon;

    @FXML
    ToggleButton selectivePayment;
    @FXML
    ToggleButton singlePayment;
    @FXML
    ToggleButton partialPayment;
    @FXML
    TextField partialPaymentValue;

    @FXML
    ToggleButton automaticGameFee;

    @FXML
    Button manualGameFee;

    @FXML
    Label payTotalPrice;

    @FXML
    protected javafx.scene.control.TableView<SoldProductsTableModel> payProductsTable;
    @FXML
    protected TableColumn payProductName;
    @FXML
    protected TableColumn payProductQuantity;
    @FXML
    protected TableColumn payProductUnitPrice;
    @FXML
    protected TableColumn payProductTotalPrice;

    private PaymentViewState paymentViewState;

    private List<ReceiptRecordView> paidProductsView;

    private ObservableList<SoldProductsTableModel> paidProductsModel;

    @Inject
    public PaymentControllerImpl(RetailServices retailServices,
                                 RestaurantServices restaurantServices,
                                 RestaurantController restaurantController) {
        super(restaurantServices, retailServices, restaurantController);
        paymentViewState = new PaymentViewState();
        paidProductsModel = FXCollections.observableArrayList();
        paidProductsView = new ArrayList<>();
    }

    @Override
    public Node getRootNode() {
        return root;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setInitialPaymentMethod();
        initializeSoldProductsTable();
        initializePayProductsTable();
        updateNode();
        initializeTableSummary();
        initializeSoldProductsTableRowHandler();
    }

    @FXML
    public void onPaymentMethodToggleAction(Event event) {
        setPaymentMethod();
    }

    @FXML
    public void onSelectivePaymentToggleAction(Event event) {
        setSelectivePayment();
    }

    @FXML
    public void onSinglePaymentToggleAction(Event event) {
        setSinglePayment();
    }

    @FXML
    public void onPartialPaymentToggleAction(Event event) {
        setPartialPayment();
    }

    @FXML
    public void onAutomaticGameFeeToggleAction(Event event) {
        setAutomaticGameFee();
    }

    @FXML
    public void onPay(Event event) {
        if(paymentViewState.isFullPayment()) {
            handleFullPayment();
        } else {
            handleSelectivePayment();
        }
    }

    private void handleFullPayment() {
        retailServices.payTable(tableView, getPaymentParams());
        updateNode();
    }

    private void handleSelectivePayment() {
        retailServices.paySelective(tableView, paidProductsView, getPaymentParams());
        paidProductsView = new ArrayList<>();
        updatePayProductsTable(convertReceiptRecordViewsToModel(paidProductsView));
        updateNode();
    }

    private void initializeSoldProductsTableRowHandler() {
        soldProductsTable.setRowFactory(tv -> {
            TableRow<SoldProductsTableModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (! row.isEmpty())) {
                    rowClickHandler(row.getItem());
                }
            });
            return row;
        });
    }

    private void rowClickHandler(SoldProductsTableModel row) {
        if(paymentViewState.isSelectivePayment()) {
            removeRowFromTable(row);
        } else if(paymentViewState.isSinglePayment()) {
            decreaseRowInTable(row, 1);
        } else if(paymentViewState.isPartialPayment()) {
            if(!isPartiallyPayable(row)) {
                return;
            }
            decreaseRowInTable(row, Double.valueOf(partialPaymentValue.getText()));
        }
    }

    private void removeRowFromTable(final SoldProductsTableModel row) {
        soldProductsModel.remove(row);
        soldProductsTable.setItems(soldProductsModel);
        List<ReceiptRecordView> matching = findMatchingView(soldProductsView, row);
        paidProductsView.add(matching.get(0));
        soldProductsView.remove(matching.get(0));
        paidProductsModel.add(row);
        updatePayProductsTable();
    }

    private void decreaseRowInTable(SoldProductsTableModel row, double amount) {
        if(row.isSingleProduct() && !paymentViewState.isPartialPayment()) {
            removeRowFromTable(row);
            return;
        }
        List<ReceiptRecordView> matchingReceiptRecordView = findMatchingView(soldProductsView, row);
        matchingReceiptRecordView.get(0).decreaseSoldQuantity(amount);

        if(decreaseClickedRow(row, amount)) {
            return; // The whole product is paid.
        }

        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductsView, row);
        if(equivalentReceiptRecordView.size() == 0) {
            ReceiptRecordView newRecord = retailServices.cloneReceiptRecordView(tableView, matchingReceiptRecordView.get(0), amount);
            paidProductsView.add(newRecord);
            createNewRow(row, newRecord, amount);
        } else {
            equivalentReceiptRecordView.get(0).increaseSoldQuantity(amount);
            List<SoldProductsTableModel> matchingRows =
                    paidProductsModel.stream().filter(thisRow -> SoldProductsTableModel.isEquals(thisRow, equivalentReceiptRecordView.get(0)))
                    .collect(Collectors.toList());
            increaseRowQuantity(matchingRows.get(0), amount);
        }
        updatePayProductsTable();
    }

    private void updatePayProductsTable() {
        payTotalPrice.setText(SoldProductsTableModel.getTotalPrice(paidProductsModel) + " Ft");
        updateSoldTotalPrice();
        payProductsTable.setItems(paidProductsModel);
        payProductsTable.refresh();
    }

    private void increaseRowQuantity(SoldProductsTableModel row, double amount) {
        paidProductsModel.remove(row);
        row.increaseProductQuantity(amount);
        paidProductsModel.add(row);
    }

    private boolean decreaseClickedRow(SoldProductsTableModel row, double amount) {
        soldProductsModel.remove(row);
        if(row.decreaseProductQuantity(amount)) {
            removeRowFromTable(row); // The whole product is paid, remove the row.
            return true;
        }
        soldProductsModel.add(row);
        soldProductsModel.sort(SoldProductsTableModel.compareById);
        soldProductsTable.setItems(soldProductsModel);
        soldProductsTable.refresh();
        return false;
    }

    private void createNewRow(SoldProductsTableModel row, ReceiptRecordView newRecord, double amount) {
        SoldProductsTableModel newRow = new SoldProductsTableModel(row);
        newRow.setProductQuantity(String.valueOf(amount));
        newRow.setProductTotalPrice(newRow.productUnitPrice);
        newRow.setProductId(String.valueOf(newRecord.getId()));
        paidProductsModel.add(newRow);
    }

    private List<ReceiptRecordView> findMatchingView(Collection<ReceiptRecordView> productsView, SoldProductsTableModel row) {
        return productsView.stream()
                .filter(receiptRecordView -> SoldProductsTableModel.isEquals(row, receiptRecordView))
                .collect(Collectors.toList());
    }

    private List<ReceiptRecordView> findEquivalentView(Collection<ReceiptRecordView> productsView, SoldProductsTableModel row) {
        return productsView.stream()
                .filter(receiptRecordView -> SoldProductsTableModel.isEquivalent(row, receiptRecordView))
                .collect(Collectors.toList());
    }

    private boolean isPartiallyPayable(SoldProductsTableModel row) {
        List<ReceiptRecordView> matchingReceiptRecordView = findMatchingView(soldProductsView, row);
        return matchingReceiptRecordView.get(0).isPartiallyPayable();
    }

    private PaymentParams getPaymentParams() {
        return PaymentParams.builder()
                        .paymentMethod(paymentViewState.getPaymentMethod())
                        .discountPercent(paymentViewState.getDiscountPercent())
                        .discountAbsolute(paymentViewState.getDiscountAbsolute())
                        .build();
    }

    private void setInitialPaymentMethod() {
        paymentMethodCash.setSelected(true);
    }

    private void updateNode() {
        updatePaymentViewState();
        soldProductsView = getSoldProducts(restaurantServices, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
    }

    private void updatePaymentViewState() {
        setPaymentMethod();
        setSelectivePayment();
        setSinglePayment();
        setPartialPayment();
        setAutomaticGameFee();
    }

    private void initializePayProductsTable() {
        payProductsTable.setEditable(true);
        payProductName.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productName"));
        payProductQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productQuantity"));
        payProductUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productUnitPrice"));
        payProductTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productTotalPrice"));
    }

    private void updatePayProductsTable(List<SoldProductsTableModel> payProducts) {
        paidProductsModel = FXCollections.observableArrayList();
        paidProductsModel.addAll(payProducts);
        payProductsTable.setItems(paidProductsModel);
    }

    private void setPaymentMethod() {
        paymentViewState.setPaymentMethod(getPaymentMethod());
    }

    private void setSelectivePayment() {
        paymentViewState.setSelectivePayment(selectivePayment.isSelected());
        paymentViewState.setSinglePayment(!selectivePayment.isSelected());
        paymentViewState.setPartialPayment(!selectivePayment.isSelected());
    }

    private void setSinglePayment() {
        paymentViewState.setSelectivePayment(!singlePayment.isSelected());
        paymentViewState.setSinglePayment(singlePayment.isSelected());
        paymentViewState.setPartialPayment(!singlePayment.isSelected());
    }

    private void setPartialPayment() {
        paymentViewState.setSelectivePayment(!partialPayment.isSelected());
        paymentViewState.setSinglePayment(!partialPayment.isSelected());
        paymentViewState.setPartialPayment(partialPayment.isSelected());
    }

    private void setAutomaticGameFee() {
        paymentViewState.setAutomaticGameFee(automaticGameFee.isSelected());
    }

    private PaymentMethod getPaymentMethod() {
        if(paymentMethodCash.isSelected()) {
            return PaymentMethod.CASH;
        } else if(paymentMethodCreditCard.isSelected()) {
            return PaymentMethod.CREDIT_CARD;
        } else if(paymentMethodCoupon.isSelected()) {
            return PaymentMethod.COUPON;
        } else return null;
    }
}
