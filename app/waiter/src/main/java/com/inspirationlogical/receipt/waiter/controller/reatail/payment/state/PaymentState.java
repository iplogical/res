package com.inspirationlogical.receipt.waiter.controller.reatail.payment.state;

import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.waiter.controller.reatail.payment.PaymentController;

public interface PaymentState {

    void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty);

    void setPaymentParams(PaymentParams paymentParams);

    void setPaymentController(PaymentController paymentController);
}
