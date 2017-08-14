package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.corelib.utility.Resources;
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

    private PaymentParams paymentParams;

    private PaymentType paymentType;

    private DiscountType discountType;

    private TextField discountAbsoluteValue;

    private TextField discountPercentValue;

    PaymentViewState(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        try {
            paymentParams = getPaymentParams();
            if(isFullPayment()) {
                handleFullPayment(soldProductsEmpty, paidProductsEmpty);
            } else if(isPartialPayment()) {
                handlePartialPayment(soldProductsEmpty, paidProductsEmpty);
            } else {
                handleSelectivePayment(soldProductsEmpty, paidProductsEmpty);
            }
        } catch (NumberFormatException e) {
            showErrorMessage();
        }
    }

    private void handlePartialPayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            paymentController.handlePartialPayment(paymentParams);
        } else {
            decideFullOrSelectivePayment(soldProductsEmpty);
        }
    }

    private void handleFullPayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            paymentController.handleFullPayment(paymentParams);
        } else {
            decideFullOrSelectivePayment(soldProductsEmpty);
        }
    }

    private void handleSelectivePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            ErrorMessage.showErrorMessage(paymentController.getRootNode(),
                    Resources.WAITER.getString("PaymentView.SelectivePaymentNoPaidProduct"));
        } else decideFullOrSelectivePayment(soldProductsEmpty);
    }

    private void decideFullOrSelectivePayment(boolean soldProductsEmpty) {
        if(soldProductsEmpty) {
            paymentController.handleFullPayment(paymentParams);
        } else {
            paymentController.handleSelectivePayment(paymentParams);
        }
    }

    private PaymentParams getPaymentParams() {
        if(isDiscountPercent()) {
            double discountPercent = Double.valueOf(discountPercentValue.getText());
            if(discountPercent < 1 || discountPercent > 100) {
                throw new NumberFormatException();
            }
        }
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

    boolean isDiscountAbsolute() {
        return discountType == ABSOLUTE;
    }

    boolean isDiscountPercent() {
        return discountType == PERCENT;
    }

    private void showErrorMessage() {
        if(isDiscountAbsolute()) {
            ErrorMessage.showErrorMessage(paymentController.getRootNode(),
                    Resources.WAITER.getString("PaymentView.DiscountAbsoluteNumberFormatError"));

        } else if(isDiscountPercent()) {
            ErrorMessage.showErrorMessage(paymentController.getRootNode(),
                    Resources.WAITER.getString("PaymentView.DiscountPercentNumberFormatError"));
        }
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
