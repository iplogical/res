package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.ProductViewImpl;

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

        return createViewsFromAdapters(getProductCategoryAdapter(category).getAllProducts());
    }
}
