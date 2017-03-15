package com.inspirationlogical.receipt.service;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.model.view.ProductCategoryViewImpl;
import com.inspirationlogical.receipt.model.view.ProductView;
import com.inspirationlogical.receipt.model.view.ProductViewImpl;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

public class CommonServicesImpl extends AbstractServices implements CommonServices {

    @Inject
    public CommonServicesImpl(EntityManager manager) {
        super(manager);
    }

    public static List<ProductView> createViewsFromAdapters(List<ProductAdapter> adapters) {
        return adapters.stream()
                .map(productAdapter -> new ProductViewImpl(productAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductView> getProducts(ProductCategoryView category) {

        return createViewsFromAdapters(((ProductCategoryViewImpl)category).getAdapter().getAllProducts());
    }
}
