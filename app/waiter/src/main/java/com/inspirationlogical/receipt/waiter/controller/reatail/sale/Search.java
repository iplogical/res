package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import java.util.List;

/**
 * Created by TheDagi on 2017. 07. 17..
 */
public interface Search {
    List<ProductView> search(String pattern);
}
