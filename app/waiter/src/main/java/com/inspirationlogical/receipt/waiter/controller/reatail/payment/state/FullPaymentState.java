package com.inspirationlogical.receipt.waiter.controller.reatail.payment.state;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

public class FullPaymentState extends AbstractPaymentState {

    @Override
    public void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            paymentController.handleFullPayment(paymentParams);
        } else {
            decideFullOrSelectivePayment(soldProductsEmpty);
        }
    }
}


