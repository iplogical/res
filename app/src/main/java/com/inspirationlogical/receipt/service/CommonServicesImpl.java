package com.inspirationlogical.receipt.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.Product;
import com.inspirationlogical.receipt.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.model.view.ProductCategoryViewImpl;
import com.inspirationlogical.receipt.model.view.ProductView;
import com.inspirationlogical.receipt.model.view.ProductViewImpl;

public class CommonServicesImpl implements CommonServices {

    public static List<ProductAdapter> createAdaptersFromAdaptees(List<Product> adaptees, EntityManager manager) {
        final List<ProductAdapter> adapters = new ArrayList<ProductAdapter>();
        adaptees.forEach((adaptee) -> {
            adapters.add(new ProductAdapter(adaptee, manager));
        });
        return adapters;
    }

    public static List<ProductView> createViewsFromAdapters(List<ProductAdapter> adapters) {
        final List<ProductView> productViews = new ArrayList<>();
        adapters.forEach((adapter)-> {
            productViews.add(new ProductViewImpl(adapter));
        });
        return productViews;
    }

    @Override
    public List<ProductView> getProducts(ProductCategoryView category) {

        return createViewsFromAdapters(((ProductCategoryViewImpl)category).getAdapter().getAllProducts());
    }
}
