package com.inspirationlogical.receipt.waiter.controller.reatail.payment.state;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.corelib.utility.ErrorMessage;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;
import javafx.scene.control.TextField;
import lombok.Data;

import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState.DiscountType.ABSOLUTE;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState.DiscountType.PERCENT;
import static com.inspirationlogical.receipt.waiter.controller.reatail.payment.state.PaymentViewState.PaymentType.*;

/**
 * Created by Bálint on 2017.03.28..
 */
public @Data class PaymentViewState {
    private PaymentController paymentController;

    private PaymentMethod paymentMethod;

    private PaymentParams paymentParams;

    private PaymentType paymentType;

    private DiscountType discountType;

    private TextField discountValue;

    private PaymentState paymentState;

    private boolean doublePrintState;

    private boolean serviceFeeState;

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
        if(isFullPayment()) {
            paymentState = new FullPaymentState();
        } else if(isPartialPayment()) {
            paymentState = new PartialPaymentState();
        } else {
            paymentState = new SelectivePaymentState();
        }
    }

    public PaymentViewState(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    public void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        try {
            paymentState.setPaymentParams(getPaymentParams());
            paymentState.setPaymentController(paymentController);
            paymentState.handlePayment(soldProductsEmpty, paidProductsEmpty);
        } catch (NumberFormatException e) {
            showErrorMessage();
        }
    }

    private PaymentParams getPaymentParams() {
        if(isDiscountPercent()) {
            double discountPercent = Double.valueOf(discountValue.getText());
            if(discountPercent < 1 || discountPercent > 100) {
                throw new NumberFormatException();
            }
        }
        return PaymentParams.builder()
                .paymentMethod(paymentMethod)
                .discountAbsolute(isDiscountAbsolute() ? Integer.valueOf(discountValue.getText()) : 0)
                .discountPercent(isDiscountPercent() ? Double.valueOf(discountValue.getText()) : 0)
                .serviceFee(serviceFeeState)
                .doublePrint(doublePrintState)
                .build();
    }

    private void showErrorMessage() {
        if(isDiscountAbsolute()) {
            ErrorMessage.showErrorMessage(paymentController.getRootNode(),
                    WaiterResources.WAITER.getString("PaymentView.DiscountAbsoluteNumberFormatError"));

        } else if(isDiscountPercent()) {
            ErrorMessage.showErrorMessage(paymentController.getRootNode(),
                    WaiterResources.WAITER.getString("PaymentView.DiscountPercentNumberFormatError"));
        }
    }

    private boolean isFullPayment() {
        return paymentType == FULL;
    }

    public boolean isSelectivePayment() {
        return paymentType == SELECTIVE;
    }

    public boolean isSinglePayment() {
        return paymentType == SINGLE;
    }

    public boolean isPartialPayment() {
        return paymentType == PARTIAL;
    }

    public boolean isDiscountAbsolute() {
        return discountType == ABSOLUTE;
    }

    public boolean isDiscountPercent() {
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

    @Override
    public String toString() {
        return "PaymentViewState{" +
                ", paymentMethod=" + paymentMethod +
                ", paymentParams=" + paymentParams +
                ", paymentType=" + paymentType +
                ", discountType=" + discountType +
                ", discounValue=" + discountValue.getText() +
                '}';
    }
}
