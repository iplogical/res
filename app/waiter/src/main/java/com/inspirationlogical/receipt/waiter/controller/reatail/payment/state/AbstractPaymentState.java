package com.inspirationlogical.receipt.waiter.controller.reatail.payment.state;

import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;
import lombok.Setter;

public abstract class AbstractPaymentState implements PaymentState {

    protected @Setter PaymentController paymentController;

    protected @Setter PaymentParams paymentParams;

    public abstract void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty);

    protected void decideFullOrSelectivePayment(boolean soldProductsEmpty) {
        if(soldProductsEmpty) {
            paymentController.handleFullPayment(paymentParams);
        } else {
            paymentController.handleSelectivePayment(paymentParams);
        }
    }
}
