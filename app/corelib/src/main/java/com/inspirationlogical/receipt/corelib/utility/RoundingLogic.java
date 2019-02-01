package com.inspirationlogical.receipt.corelib.utility;

import com.inspirationlogical.receipt.corelib.model.enums.PaymentMethod;

/**
 * Created by Ferenc on 2017. 04. 14..
 */
public interface RoundingLogic {
    double round(double d);

    static boolean roundingNeeded(PaymentMethod method){
        return method == PaymentMethod.CASH || method == PaymentMethod.COUPON;
    }

    static RoundingLogic create(PaymentMethod method) {
        if (roundingNeeded(method)) {
            return new CashRoundingLogic();
        }
        else return new NoRoundingLogic();
    }
}


class CashRoundingLogic implements RoundingLogic {

    @Override
    public double round(double amount) {
        double base = ((int) (amount / 10.0)) * 10.0;
        double residue = amount - base;
        double additiveCorrection = 0.0;
        if (residue > 2.49 && residue <= 7.49) {
            additiveCorrection = 5;
        } else if (residue > 7.49) {
            additiveCorrection = 10;
        }
        return base + additiveCorrection;
    }
}

class NoRoundingLogic implements RoundingLogic {

    @Override
    public double round(double amount) {
        return amount;
    }
}
