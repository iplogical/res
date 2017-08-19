package com.inspirationlogical.receipt.corelib.service;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.adapter.*;
import com.inspirationlogical.receipt.corelib.model.view.*;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

abstract class AbstractService {

    List<ProductView> productViews;

    List<ProductCategoryView> categoryViews;

    static <View, Adapter> List<View> createViewsFromAdapters(List<Adapter> adapters, Function<Adapter, View> constructor) {
        return adapters.stream()
                .map(constructor)
                .collect(Collectors.toList());
    }

    AbstractService() {
        productViews = createViewsFromAdapters(ProductAdapter.getActiveProducts(), ProductViewImpl::new);
        categoryViews = createViewsFromAdapters(ProductCategoryAdapter.getProductCategories(), ProductCategoryViewImpl::new);
    }

    RestaurantAdapter getRestaurantAdapter(RestaurantView restaurant) {
        return ((RestaurantViewImpl)restaurant).getAdapter();
    }

    TableAdapter getTableAdapter(TableView tableView) {
        return ((TableViewImpl)tableView).getAdapter();
    }

    ProductAdapter getProductAdapter(ProductView productView) {
        return ((ProductViewImpl)productView).getAdapter();
    }

    ProductCategoryAdapter getProductCategoryAdapter(ProductCategoryView category) {
        return ((ProductCategoryViewImpl)category).getAdapter();
    }

    ReceiptAdapter getReceiptAdapter(ReceiptView receiptView) {
        return ((ReceiptViewImpl)receiptView).getAdapter();
    }

    ReceiptRecordAdapter getReceiptRecordAdapter(ReceiptRecordView receiptRecordView) {
        return ((ReceiptRecordViewImpl)receiptRecordView).getAdapter();
    }

    ReservationAdapter getReservationAdapter(ReservationView reservationView) {
        return ((ReservationViewImpl)reservationView).getAdapter();
    }
}
