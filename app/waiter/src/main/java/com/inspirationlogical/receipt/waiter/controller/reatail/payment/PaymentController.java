package com.inspirationlogical.receipt.waiter.controller.reatail.payment;

import com.inspirationlogical.receipt.corelib.params.PaymentParams;
import com.inspirationlogical.receipt.waiter.controller.reatail.AbstractRetailController;

/**
 * Created by Bálint on 2017.03.28..
 */
public interface PaymentController extends AbstractRetailController {

    void enterPaymentView();

    void handleFullPayment(PaymentParams paymentParams);

    void handleSelectivePayment(PaymentParams paymentParams);
}
