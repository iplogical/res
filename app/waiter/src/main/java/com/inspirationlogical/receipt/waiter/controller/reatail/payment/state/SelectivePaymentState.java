package com.inspirationlogical.receipt.waiter.controller.reatail.payment.state;

import com.inspirationlogical.receipt.corelib.utility.NotificationMessage;
import com.inspirationlogical.receipt.waiter.utility.WaiterResources;

public class SelectivePaymentState extends AbstractPaymentState {

    @Override
    public void handlePayment(boolean soldProductsEmpty, boolean paidProductsEmpty) {
        if(paidProductsEmpty) {
            NotificationMessage.showErrorMessage(paymentController.getRootNode(),
                    WaiterResources.WAITER.getString("PaymentView.SelectivePaymentNoPaidProduct"));
        } else {
            decideFullOrSelectivePayment(soldProductsEmpty);
        }
    }
}
