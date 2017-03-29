package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.entity.ReceiptRecord;
import com.inspirationlogical.receipt.corelib.model.view.*;

import javax.persistence.EntityManager;

public abstract class AbstractServices {

    protected EntityManager manager;

    protected AbstractServices(EntityManager manager) {
        this.manager = manager;
    }

    protected RestaurantAdapter getRestaurantAdapter(RestaurantView restaurant) {
        return ((RestaurantViewImpl)restaurant).getAdapter();
    }

    protected TableAdapter getTableAdapter(TableView tableView) {
        return ((TableViewImpl)tableView).getAdapter();
    }

    protected ProductAdapter getProductAdapter(ProductView productView) {
        return ((ProductViewImpl)productView).getAdapter();
    }

    protected ProductCategoryAdapter getProductCategoryAdapter(ProductCategoryView category) {
        return ((ProductCategoryViewImpl)category).getAdapter();
    }

    protected ReceiptRecordAdapter getReceiptRecordAdapter(ReceiptRecordView receiptRecordView) {
        return ((ReceiptRecordViewImpl)receiptRecordView).getAdapter();
    }
}
