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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Bálint on 2017.03.28..
 */
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
    ToggleButton partialPayment;
    @FXML
    ToggleButton automaticGameFee;

    @FXML
    Button manualGameFee;

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

    private List<ReceiptRecordView> payProducts;

    private ObservableList<SoldProductsTableModel> payProductList;

    @Inject
    public PaymentControllerImpl(RetailServices retailServices,
                                 RestaurantServices restaurantServices,
                                 RestaurantController restaurantController) {
        super(restaurantServices, retailServices, restaurantController);
        paymentViewState = new PaymentViewState();
        payProductList = FXCollections.observableArrayList();
        payProducts = new ArrayList<>();
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

    private void initializeSoldProductsTableRowHandler() {
        soldProductsTable.setRowFactory(tv -> {
            TableRow<SoldProductsTableModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (! row.isEmpty())) {
                    if(!paymentViewState.isSelectivePayment()) {
                        return;
                    }
                    removeRowFromTable(row.getItem());
                }
            });
            return row;
        });
    }

    private void removeRowFromTable(final SoldProductsTableModel row) {
        soldProductList.remove(row);
        soldProductsTable.setItems(soldProductList);
        List<ReceiptRecordView> matching = soldProducts.stream()
                .filter(receiptRecordView -> receiptRecordView.getName().equals(row.getProductName()) &&
                            String.valueOf(receiptRecordView.getSoldQuantity()).equals(row.getProductQuantity()) &&
                            String.valueOf(receiptRecordView.getSalePrice()).equals(row.getProductUnitPrice()) &&
                            String.valueOf(receiptRecordView.getTotalPrice()).equals(row.getProductTotalPrice()))
                .collect(Collectors.toList());
        payProducts.add(matching.get(0));
        soldProducts.remove(matching.get(0));
        payProductList.add(row);
        payProductsTable.setItems(payProductList);
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
    public void onPartialPaymentToggleAction(Event event) {
        setPartialPayment();
    }

    @FXML
    public void onAutomaticGameFeeToggleAction(Event event) {
        setAutomaticGameFee();
    }

    @FXML
    public void onPay(Event event) {
        if(paymentViewState.isSelectivePayment()) {
            retailServices.paySelective(tableView, payProducts, getPaymentParams());
            payProducts = new ArrayList<>();
            updatePayProductsTable(convertReceiptRecordViewsToModel(payProducts));
            updateNode();
        } else {
            retailServices.payTable(tableView, getPaymentParams());
            updateNode();
        }
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
        soldProducts = getSoldProducts(restaurantServices, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProducts));
    }

    private void updatePaymentViewState() {
        setPaymentMethod();
        setSelectivePayment();
        setPartialPayment();
        setAutomaticGameFee();
    }

    private void setPaymentMethod() {
        paymentViewState.setPaymentMethod(getPaymentMethod());
    }

    private void setSelectivePayment() {
        paymentViewState.setSelectivePayment(selectivePayment.isSelected());
    }

    private void setPartialPayment() {
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

    protected void initializePayProductsTable() {
        payProductsTable.setEditable(true);
        payProductName.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productName"));
        payProductQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productQuantity"));
        payProductUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productUnitPrice"));
        payProductTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productTotalPrice"));
    }

    protected void updatePayProductsTable(List<SoldProductsTableModel> payProducts) {
        payProductList = FXCollections.observableArrayList();
        payProductList.addAll(payProducts);
        payProductsTable.setItems(payProductList);
    }
}
