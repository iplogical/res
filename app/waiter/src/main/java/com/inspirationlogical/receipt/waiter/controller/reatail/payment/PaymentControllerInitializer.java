package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState;
import com.inspirationlogical.receipt.waiter.viewmodel.ProductRowModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableRow;
import javafx.scene.control.Toggle;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState.PaymentType.*;

public class PaymentControllerInitializer {

    final private static Logger logger = LoggerFactory.getLogger(PaymentControllerImpl.class);

    private PaymentControllerImpl p;

    public PaymentControllerInitializer(PaymentControllerImpl p) {
        this.p = p;
    }

    public void initialize() {
        initializeToggleGroups();
        initializePaidProductsTable();
        initializePaidProductsTableRowHandler();
        initializePaymentViewState();
        initializePaidTotalPrices();
        p.initLiveTime(p.liveTime);
    }

    private void initializeToggleGroups() {
        initializePaymentMethodToggles();
        initializePaymentTypeToggles();
        initializeDiscountToggles();
        initializeDoublePrint();
        initializeServiceFee();
    }

    private void initializePaymentMethodToggles() {
        p.paymentMethodCash.setUserData(PaymentMethod.CASH);
        p.paymentMethodCreditCard.setUserData(PaymentMethod.CREDIT_CARD);
        p.paymentMethodCoupon.setUserData(PaymentMethod.COUPON);
        p.paymentMethodCash.setSelected(true);
        p.paymentViewState.setPaymentMethod(PaymentMethod.CASH);
        p.paymentMethodToggleGroup.selectedToggleProperty().addListener(new PaymentMethodToggleListener());
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

    private void initializeDoublePrint() {
        p.doublePrint.setSelected(false);
        p.doublePrint.selectedProperty().addListener(new DoublePrintToggleListener());
    }

    private void initializeServiceFee() {
        p.serviceFee.setSelected(true);
        p.paymentViewState.setServiceFeeState(true);
        p.serviceFee.selectedProperty().addListener(new ServiceFeeToggleListener());
    }

    private void initializePaidProductsTable() {
        p.paidProductsTable.setEditable(true);
        p.payProductName.setCellValueFactory(new PropertyValueFactory<ProductRowModel, String>("productName"));
        p.payProductQuantity.setCellValueFactory(new PropertyValueFactory<ProductRowModel, String>("productQuantity"));
        p.payProductUnitPrice.setCellValueFactory(new PropertyValueFactory<ProductRowModel, String>("productUnitPrice"));
        p.payProductTotalPrice.setCellValueFactory(new PropertyValueFactory<ProductRowModel, String>("productTotalPrice"));
    }

    private void initializePaidProductsTableRowHandler() {
        p.paidProductsTable.setRowFactory(tv -> {
            TableRow<ProductRowModel> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 1 && (! row.isEmpty())) {
                    p.onPaidProductsRowClicked(row.getItem());
                }
            });
            return row;
        });
    }

    private void initializePaymentViewState() {
        p.paymentViewState.setDiscountValue(p.discountValue);
    }

    private void initializePaidTotalPrices() {
        p.paidTotalPrice.setText("0 Ft");
        p.previousPartialPrice.setText("0 Ft");
    }

    private class PaymentTypeTogglesListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if(p.paymentTypeToggleGroup.getSelectedToggle() == null) {
                logger.info("The payment type changed to FULL.");
                p.paymentViewState.setPaymentType(PaymentViewState.PaymentType.FULL);
                return;
            }
            logger.info("The payment type changed to: " + p.paymentTypeToggleGroup.getSelectedToggle().getUserData());
            p.paymentViewState.setPaymentType(
                    (PaymentViewState.PaymentType) p.paymentTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }

    private class DiscountTypeTogglesListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (p.discountTypeToggleGroup.getSelectedToggle() == null) {
                logger.info("The discount type changed to NONE.");
                p.paymentViewState.setDiscountType(PaymentViewState.DiscountType.NONE);
                return;
            }
            logger.info("The discount type changed to: " + p.discountTypeToggleGroup.getSelectedToggle().getUserData());
            p.paymentViewState.setDiscountType(
                    (PaymentViewState.DiscountType) p.discountTypeToggleGroup.getSelectedToggle().getUserData());
        }
    }

    private class PaymentMethodToggleListener implements ChangeListener<Toggle> {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            logger.info("The payment method changed to :" + p.paymentMethodToggleGroup.getSelectedToggle().getUserData());
            p.paymentViewState.setPaymentMethod(
                    (PaymentMethod) p.paymentMethodToggleGroup.getSelectedToggle().getUserData());
        }
    }

    private class ServiceFeeToggleListener implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            logger.info("The service fee toggle changed to :" + newValue);
            p.paymentViewState.setServiceFeeState(newValue);
        }
    }

    private class DoublePrintToggleListener implements ChangeListener<Boolean> {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            logger.info("The double print toggle changed to :" + newValue);
            p.paymentViewState.setDoublePrintState(newValue);
        }
    }
}
