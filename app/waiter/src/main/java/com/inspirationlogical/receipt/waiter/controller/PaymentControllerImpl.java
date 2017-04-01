package com.inspirationlogical.receipt.waiter.controller;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.service.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantServices;
import com.inspirationlogical.receipt.corelib.service.RetailServices;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.application.WaiterApp;
import com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.PaymentType.*;

@Singleton
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
    TextField partialPaymentValue;

    @FXML
    ToggleButton automaticGameFee;

    @FXML
    Button manualGameFee;

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

    private SaleController saleController;

    private PaymentViewState paymentViewState;

    private List<ReceiptRecordView> paidProductsView;

    private ObservableList<SoldProductsTableModel> paidProductsModel;

    @Inject
    public PaymentControllerImpl(RetailServices retailServices,
                                 RestaurantServices restaurantServices,
                                 RestaurantController restaurantController,
                                 SaleController saleController) {
        super(restaurantServices, retailServices, restaurantController);
        this.saleController = saleController;
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
        initializeToggleGroups();
        initializeSoldProductsTable();
        initializePayProductsTable();
        setAutomaticGameFee();
        getSoldProductsAndUpdateTable();
        initializeTableSummary();
        initializeSoldProductsTableRowHandler();
    }

    @FXML
    public void onBackToSaleView(Event event) {
        Parent root = (Parent) saleController.getRootNode();
        WaiterApp.getWindow().getScene().setRoot(root);
    }

    @FXML
    public void onAutomaticGameFeeToggleAction(Event event) {
        setAutomaticGameFee();
    }

    @FXML
    public void onManualGameFee(Event event) {
        retailServices.sellGameFee(tableView, 1);
        soldProductsView = getSoldProducts(restaurantServices, tableView);
        removeRowFromTable(convertReceiptRecordViewsToModel(soldProductsView.stream()
                .sorted(Comparator.comparing(ReceiptRecordView::getId).reversed())
                .filter(receiptRecordView -> receiptRecordView.getName().equals(Resources.CONFIG.getString("Product.GameFeeName")))
                .limit(1)
                .collect(Collectors.toList())).get(0));
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
        handleAutomaticGameFee();
        retailServices.payTable(tableView, getPaymentParams());
        getSoldProductsAndUpdateTable();
    }

    private void handleSelectivePayment() {
        retailServices.paySelective(tableView, paidProductsView, getPaymentParams());
        paidProductsView = new ArrayList<>();
        updatePayProductsTable(convertReceiptRecordViewsToModel(paidProductsView));
        getSoldProductsAndUpdateTable();
    }

    private void handleAutomaticGameFee() {
        if(paymentViewState.isAutomaticGameFee()) {
            //TODO: Make the 2000 configurable from the manager terminal.
            int guestCount = tableView.getGuestCount();
            int price = (int)receiptView.getTotalPrice();
            int requiredGameFee = (guestCount * 2000 - price) / 2000;
            if(requiredGameFee > 0) {
                retailServices.sellGameFee(tableView, requiredGameFee);
            }
        }
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
                        .discountAbsolute(paymentViewState.isDiscountAbsolute() ? Integer.valueOf(discountAbsoluteValue.getText()) : 0)
                        .discountPercent(paymentViewState.isDiscountPercent() ? Double.valueOf(discountPercentValue.getText()) : 0)
                        .build();
    }

    private void initializeToggleGroups() {
        initializePaymentMethodToggles();
        initializePaymentTypeToggles();
        initializeDiscountToggles();
    }

    private void initializePaymentMethodToggles() {
        paymentMethodCash.setUserData(PaymentMethod.CASH);
        paymentMethodCreditCard.setUserData(PaymentMethod.CREDIT_CARD);
        paymentMethodCoupon.setUserData(PaymentMethod.COUPON);
        paymentMethodCash.setSelected(true);
        paymentViewState.setPaymentMethod(PaymentMethod.CASH);
        paymentMethodToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                paymentViewState.setPaymentMethod((PaymentMethod)paymentMethodToggleGroup.getSelectedToggle().getUserData());
            }
        });
    }

    private void initializePaymentTypeToggles() {
        selectivePayment.setUserData(SELECTIVE);
        singlePayment.setUserData(SINGLE);
        partialPayment.setUserData(PARTIAL);
        paymentViewState.setPaymentType(FULL);
        paymentTypeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(paymentTypeToggleGroup.getSelectedToggle() == null) {
                    paymentViewState.setPaymentType(FULL);
                    return;
                }
                paymentViewState.setPaymentType((PaymentViewState.PaymentType)paymentTypeToggleGroup.getSelectedToggle().getUserData());
            }
        });
    }

    private void initializeDiscountToggles() {
        discountAbsolute.setUserData(PaymentViewState.DiscountType.ABSOLUTE);
        discountPercent.setUserData(PaymentViewState.DiscountType.PERCENT);
        paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
        discountTypeToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(discountTypeToggleGroup.getSelectedToggle() == null) {
                    paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
                }
                paymentViewState.setDiscountType((PaymentViewState.DiscountType)discountTypeToggleGroup.getSelectedToggle().getUserData());
            }
        });
    }

    private void initializePayProductsTable() {
        payProductsTable.setEditable(true);
        payProductName.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productName"));
        payProductQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productQuantity"));
        payProductUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productUnitPrice"));
        payProductTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductsTableModel, String>("productTotalPrice"));
    }

    private void getSoldProductsAndUpdateTable() {
        soldProductsView = getSoldProducts(restaurantServices, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
    }

    private void updatePayProductsTable(List<SoldProductsTableModel> payProducts) {
        paidProductsModel = FXCollections.observableArrayList();
        paidProductsModel.addAll(payProducts);
        payProductsTable.setItems(paidProductsModel);
    }

    private void setAutomaticGameFee() {
        paymentViewState.setAutomaticGameFee(automaticGameFee.isSelected());
    }
}
