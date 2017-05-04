package com.inspirationlogical.receipt.corelib.service;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.view.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractService {

    protected EntityManager manager;

    protected List<ProductView> productViews;

    protected List<ProductCategoryView> categoryViews;


    protected static <View, Adapter> List<View> createViewsFromAdapters(List<Adapter> adapters, Function<Adapter, View> constructor) {
        return adapters.stream()
                .map(constructor)
                .collect(Collectors.toList());
    }

    protected AbstractService(EntityManager manager) {
        this.manager = manager;
        productViews = ProductAdapter.getProducts();
        categoryViews = ProductCategoryAdapter.getProductCategories();
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

    protected ReceiptAdapter getReceiptAdapter(ReceiptView receiptView) {
        return ((ReceiptViewImpl)receiptView).getAdapter();
    }

    protected ReceiptRecordAdapter getReceiptRecordAdapter(ReceiptRecordView receiptRecordView) {
        return ((ReceiptRecordViewImpl)receiptRecordView).getAdapter();
    }

    protected ReservationAdapter getReservationAdapter(ReservationView reservationView) {
        return ((ReservationViewImpl)reservationView).getAdapter();
    }
}
