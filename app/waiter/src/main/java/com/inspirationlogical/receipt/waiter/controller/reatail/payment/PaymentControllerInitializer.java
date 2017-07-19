package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.waiter.viewmodel.SoldProductViewModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableRow;
import javafx.scene.control.Toggle;
import javafx.scene.control.cell.PropertyValueFactory;

import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.*;

public class PaymentControllerInitializer {

    private PaymentControllerImpl p;

    public PaymentControllerInitializer(PaymentControllerImpl p) {
        this.p = p;
    }

    public void initialize() {
        initializeToggleGroups();
        initializePaidProductsTable();
        initializePaidProductsTableRowHandler();
        initializePaymentViewState();
        p.initLiveTime(p.liveTime);
    }

    private void initializeToggleGroups() {
        initializePaymentMethodToggles();
        initializePaymentTypeToggles();
        initializeDiscountToggles();
    }

    private void initializePaymentMethodToggles() {
        p.paymentMethodCash.setUserData(PaymentMethod.CASH);
        p.paymentMethodCreditCard.setUserData(PaymentMethod.CREDIT_CARD);
        p.paymentMethodCoupon.setUserData(PaymentMethod.COUPON);
        p.paymentMethodCash.setSelected(true);
        p.paymentViewState.setPaymentMethod(PaymentMethod.CASH);
        p.paymentMethodToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                p.paymentViewState.setPaymentMethod(
                        (PaymentMethod) p.paymentMethodToggleGroup.getSelectedToggle().getUserData());
            }
        });
    }

    private void initializePaymentTypeToggles() {
        p.selectivePayment.setUserData(SELECTIVE);
        p.singlePayment.setUserData(SINGLE);
        p.partialPayment.setUserData(PARTIAL);
        p.paymentViewState.setPaymentType(FULL);
        p.paymentTypeToggleGroup.selectedToggleProperty().addListener(new PaymentTypeTogglesListener());
    }

    private void initializeDiscountToggles() {
        p.discountAbsolute.setUserData(PaymentViewState.DiscountType.ABSOLUTE);
        p.discountPercent.setUserData(PaymentViewState.DiscountType.PERCENT);
        p.paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
        p.discountTypeToggleGroup.selectedToggleProperty().addListener(new DiscountTypeTogglesListener());
    }

    private void initializePaidProductsTable() {
        p.paidProductsTable.setEditable(true);
        p.payProductName.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productName"));
        p.payProductQuantity.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productQuantity"));
        p.payProductUnitPrice.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productUnitPrice"));
        p.payProductTotalPrice.setCellValueFactory(new PropertyValueFactory<SoldProductViewModel, String>("productTotalPrice"));
    }

    private void initializePaidProductsTableRowHandler() {
        p.paidProductsTable.setRowFactory(tv -> {
            TableRow<SoldProductViewModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1 && (! row.isEmpty())) {
                    p.paidProductsRowClickHandler(row.getItem());
                }
            });
            return row;
        });
    }

    private void initializePaymentViewState() {
        p.paymentViewState.setDiscountAbsoluteValue(p.discountAbsoluteValue);
        p.paymentViewState.setDiscountPercentValue(p.discountPercentValue);
    }

    private class PaymentTypeTogglesListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if(p.paymentTypeToggleGroup.getSelectedToggle() == null) {
                p.paymentViewState.setPaymentType(PaymentViewState.PaymentType.FULL);
                return;
            }
            p.paymentViewState.setPaymentType(
                    (PaymentViewState.PaymentType) p.paymentTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }

    private class DiscountTypeTogglesListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (p.discountTypeToggleGroup.getSelectedToggle() == null) {
                p.paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
                return;
            }
            p.paymentViewState.setDiscountType(
                    (PaymentViewState.DiscountType) p.discountTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }
}
