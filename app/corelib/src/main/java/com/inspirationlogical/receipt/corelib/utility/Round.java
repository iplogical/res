package com.inspirationlogical.receipt.corelib.utility;

/**
 * Created by TheDagi on 2017. 04. 14..
 */
public class Round {

    public static double roundToTwoDecimals(double number) {
        double rounded = Math.round(number * 100);
        return rounded / 100;
    }
}
