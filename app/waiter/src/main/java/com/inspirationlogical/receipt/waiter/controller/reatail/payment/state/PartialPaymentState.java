package com.inspirationlogical.receipt.waiter.controller.reatail.payment.state;

public class PartialPaymentState  extends AbstractPaymentState {

    @Override
    public void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            paymentController.handlePartialPayment(paymentParams);
        } else {
            decideFullOrSelectivePayment(soldProductsEmpty);
        }
    }
}
