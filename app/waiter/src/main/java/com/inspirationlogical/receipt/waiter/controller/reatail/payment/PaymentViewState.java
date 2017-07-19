package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import javafx.scene.control.TextField;
import lombok.Data;

import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.DiscountType.ABSOLUTE;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.DiscountType.PERCENT;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentViewState.PaymentType.*;

/**
 * Created by Bálint on 2017.03.28..
 */
public @Data class PaymentViewState {
    private PaymentController paymentController;

    private PaymentMethod paymentMethod;

    private PaymentType paymentType;

    private DiscountType discountType;

    private TextField discountAbsoluteValue;

    private TextField discountPercentValue;

    PaymentViewState(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(isFullPayment()) {
            handleFullPayment(soldProductsEmpty, paidProductsEmpty);
        } else {
            handleSelectivePayment(soldProductsEmpty, paidProductsEmpty);
        }
    }

    private void handleFullPayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            paymentController.handleFullPayment(getPaymentParams());
        } else {
            if(soldProductsEmpty) {
                paymentController.handleFullPayment(getPaymentParams());
            } else {
                paymentController.handleSelectivePayment(getPaymentParams());
            }
        }
    }

    private void handleSelectivePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            return;
        } else if(soldProductsEmpty) {
            paymentController.handleFullPayment(getPaymentParams());
        } else {
            paymentController.handleSelectivePayment(getPaymentParams());
        }
    }

    private PaymentParams getPaymentParams() {
        return PaymentParams.builder()
                .paymentMethod(paymentMethod)
                .discountAbsolute(isDiscountAbsolute() ? Integer.valueOf(discountAbsoluteValue.getText()) : 0)
                .discountPercent(isDiscountPercent() ? Double.valueOf(discountPercentValue.getText()) : 0)
                .build();
    }

    private boolean isFullPayment() {
        return paymentType == FULL;
    }

    boolean isSelectivePayment() {
        return paymentType == SELECTIVE;
    }

    boolean isSinglePayment() {
        return paymentType == SINGLE;
    }

    boolean isPartialPayment() {
        return paymentType == PARTIAL;
    }

    private boolean isDiscountAbsolute() {
        return discountType == ABSOLUTE;
    }

    private boolean isDiscountPercent() {
        return discountType == PERCENT;
    }

    public enum PaymentType {
        FULL,
        SELECTIVE,
        SINGLE,
        PARTIAL
    }

    public enum DiscountType {
        NONE,
        ABSOLUTE,
        PERCENT
    }
}
