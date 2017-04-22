package com.inspirationlogical.receipt.waiter.controller;

import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.PaymentType.FULL;
import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.PaymentType.PARTIAL;
import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.PaymentType.SELECTIVE;
import static com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState.PaymentType.SINGLE;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.inspirationlogical.receipt.corelib.frontend.view.ViewLoader;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.corelib.model.view.ReceiptRecordView;
import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.service.RestaurantService;
import com.inspirationlogical.receipt.corelib.service.RetailService;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import com.inspirationlogical.receipt.waiter.viewstate.PaymentViewState;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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
    protected javafx.scene.control.TableView<SoldProductViewModel> payProductsTable;
    @FXML
    protected TableColumn payProductName;
    @FXML
    protected TableColumn payProductQuantity;
    @FXML
    protected TableColumn payProductUnitPrice;
    @FXML
    protected TableColumn payProductTotalPrice;

    @Inject
    private ViewLoader viewLoader;

    private SaleController saleController;

    private PaymentViewState paymentViewState;

    private List<ReceiptRecordView> paidProductsView;

    private ObservableList<SoldProductViewModel> paidProductsModel;

    @Inject
    public PaymentControllerImpl(RetailService retailService,
                                 RestaurantService restaurantService,
                                 RestaurantController restaurantController,
                                 SaleController saleController) {
        super(restaurantService, retailService, restaurantController);
        this.saleController = saleController;
        paymentViewState = new PaymentViewState();
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
        initializeToggleGroups();
        initializeSoldProductsTable();
        initializePayProductsTable();
        setAutomaticGameFee();
        getSoldProductsAndUpdateTable();
        updateTableSummary();
        initializeSoldProductsTableRowHandler();
    }

    @Override
    public void updateNode() {
        if(!soldProductsTableInitialized) {
            return;
        }
        getSoldProductsAndUpdateTable();
        updateTableSummary();
    }

    @Override
    public void onBackToRestaurantView(Event event) {
        discardPaidRecords();
        backToRestaurantView();
    }


    @Override
    protected void rowClickHandler(SoldProductViewModel row) {
        if(paymentViewState.isSelectivePayment()) {
            addRowToPaidProducts(row, removeRowFromSoldProducts(row));
        } else if(paymentViewState.isSinglePayment()) {
            if(row.isSingleProduct()) {
                addRowToPaidProducts(row, removeRowFromSoldProducts(row));
            } else {
                increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, 1), 1);
            }
        } else if(paymentViewState.isPartialPayment()) {
            if(!isPartiallyPayable(row)) {
                return;
            }
            double amount = Double.valueOf(partialPaymentValue.getText());
            increaseRowInPaidProducts(row, decreaseRowInSoldProducts(row, amount), amount);
        }
    }

    @FXML
    public void onBackToSaleView(Event event) {
        discardPaidRecords();
        saleController.updateNode();
        viewLoader.loadViewIntoScene(saleController);
    }

    @FXML
    public void onAutomaticGameFeeToggleAction(Event event) {
        setAutomaticGameFee();
    }

    @FXML
    public void onManualGameFee(Event event) {
        retailService.sellGameFee(tableView, 1);
        soldProductsView = getSoldProducts(restaurantService, tableView);
        ReceiptRecordView gameFee = (soldProductsView.stream()
                .sorted(Comparator.comparing(ReceiptRecordView::getId).reversed())
                .filter(receiptRecordView -> receiptRecordView.getName().equals(Resources.CONFIG.getString("Product.GameFeeName")))
                .limit(1)
                .collect(Collectors.toList())).get(0);
        Collection<ReceiptRecordView> gameFeeList = new ArrayList<>(Arrays.asList(gameFee));
        removeRowFromSoldProducts(new SoldProductViewModel(gameFee));
        addRowToPaidProducts(new SoldProductViewModel(gameFee), gameFee);
    }

    @FXML
    public void onPay(Event event) {
        if(paymentViewState.isFullPayment()) {
            if(!isPaidProductsEmpty()) {
                if(isSoldProductsEmpty()) {
                    handleFullPayment();
                } else {
                    handleSelectivePayment();
                }
            } else {
                handleFullPayment();
            }
        } else {
            if(isPaidProductsEmpty()) {
                return;
            } else if(isSoldProductsEmpty()) {
                handleFullPayment();
            } else {
                handleSelectivePayment();
            }
        }
    }

    private boolean isSoldProductsEmpty() {
        return soldProductsView.size() == 0;
    }

    private boolean isPaidProductsEmpty() {
        return paidProductsView.size() == 0;
    }

    private void handleFullPayment() {
        handleAutomaticGameFee();
        retailService.payTable(tableView, getPaymentParams());
        getSoldProductsAndUpdateTable();
        discardPaidRecords();
        backToRestaurantView();
    }

    private void handleSelectivePayment() {
        retailService.paySelective(tableView, paidProductsView, getPaymentParams());
        discardPaidRecords();
        getSoldProductsAndUpdateTable();
    }

    private void handleAutomaticGameFee() {
        if(paymentViewState.isAutomaticGameFee()) {
            //TODO: Make the 2000 configurable from the manager terminal.
            int guestCount = tableView.getGuestCount();
            int price = (int)receiptView.getTotalPrice();
            int requiredGameFee = ((guestCount * 2000 - price) / 2000) + 1;
            if(requiredGameFee > 0) {
                retailService.sellGameFee(tableView, requiredGameFee);
            }
        }
    }

    private void addRowToPaidProducts(final SoldProductViewModel row, ReceiptRecordView toAdd) {
        paidProductsView.add(toAdd);
        paidProductsModel.add(row);
        updatePayProductsTable();
    }

    private void increaseRowInPaidProducts(SoldProductViewModel row, ReceiptRecordView toAdd, double amount) {
        List<ReceiptRecordView> equivalentReceiptRecordView = findEquivalentView(paidProductsView, row);
        if(equivalentReceiptRecordView.size() == 0) {
            ReceiptRecordView newRecord = retailService.cloneReceiptRecordView(tableView, toAdd, amount);
            paidProductsView.add(newRecord);
            createNewRow(row, newRecord, amount);
        } else {
            equivalentReceiptRecordView.get(0).increaseSoldQuantity(amount);
            List<SoldProductViewModel> matchingRows =
                    paidProductsModel.stream().filter(thisRow -> SoldProductViewModel.isEquals(thisRow, equivalentReceiptRecordView.get(0)))
                    .collect(Collectors.toList());
            increaseRowQuantity(matchingRows.get(0), amount);
        }
        updatePayProductsTable();
    }

    private void updatePayProductsTable() {
        payTotalPrice.setText(SoldProductViewModel.getTotalPrice(paidProductsModel) + " Ft");
        updateSoldTotalPrice();
        payProductsTable.setItems(paidProductsModel);
        payProductsTable.refresh();
    }

    private void increaseRowQuantity(SoldProductViewModel row, double amount) {
        paidProductsModel.remove(row);
        row.increaseProductQuantity(amount);
        paidProductsModel.add(row);
    }

    private void createNewRow(SoldProductViewModel row, ReceiptRecordView newRecord, double amount) {
        SoldProductViewModel newRow = new SoldProductViewModel(row);
        newRow.setProductQuantity(String.valueOf(amount));
        newRow.setProductTotalPrice(String.valueOf((int)(Integer.valueOf(newRow.getProductUnitPrice()) * amount)));
        newRow.setProductId(String.valueOf(newRecord.getId()));
        paidProductsModel.add(newRow);
    }

    private List<ReceiptRecordView> findEquivalentView(Collection<ReceiptRecordView> productsView, SoldProductViewModel row) {
        return productsView.stream()
                .filter(receiptRecordView -> SoldProductViewModel.isEquivalent(row, receiptRecordView))
                .collect(Collectors.toList());
    }

    private boolean isPartiallyPayable(SoldProductViewModel row) {
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
        paymentTypeToggleGroup.selectedToggleProperty().addListener(new PaymentTypeTogglesListener());
    }

    private void initializeDiscountToggles() {
        discountAbsolute.setUserData(PaymentViewState.DiscountType.ABSOLUTE);
        discountPercent.setUserData(PaymentViewState.DiscountType.PERCENT);
        paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
        discountTypeToggleGroup.selectedToggleProperty().addListener(new DiscountTypeTogglesListener());
    }

    private void initializePayProductsTable() {
        payProductsTable.setEditable(true);
        payProductName.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productName"));
        payProductQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productQuantity"));
        payProductUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productUnitPrice"));
        payProductTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productTotalPrice"));
    }

    private void getSoldProductsAndUpdateTable() {
        soldProductsView = getSoldProducts(restaurantService, tableView);
        updateSoldProductsTable(convertReceiptRecordViewsToModel(soldProductsView));
    }

    private void discardPaidRecords() {
        paidProductsModel = FXCollections.observableArrayList();
        paidProductsView = new ArrayList<>();
        updatePayProductsTable();
    }

    private void backToRestaurantView() {
        restaurantController.updateRestaurant();
        viewLoader.loadViewIntoScene(restaurantController);
    }

    private void setAutomaticGameFee() {
        paymentViewState.setAutomaticGameFee(automaticGameFee.isSelected());
    }

    private class PaymentTypeTogglesListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if(paymentTypeToggleGroup.getSelectedToggle() == null) {
                paymentViewState.setPaymentType(PaymentViewState.PaymentType.FULL);
                return;
            }
            paymentViewState.setPaymentType((PaymentViewState.PaymentType)paymentTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }

    private class DiscountTypeTogglesListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (discountTypeToggleGroup.getSelectedToggle() == null) {
                paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
                return;
            }
            paymentViewState.setDiscountType((PaymentViewState.DiscountType) discountTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }
}
