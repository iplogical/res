package com.inspirationlogical.receipt.corelib.model.view;

import java.util.Comparator;

/**
 * Created by Bálint on 2017.03.23..
 */
public interface AbstractView {

    default int getOrderNumber() {return 0;}

    String getName();
}
