package com.inspirationlogical.receipt.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.entity.Product;
import com.inspirationlogical.receipt.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.model.view.ProductCategoryViewImpl;
import com.inspirationlogical.receipt.model.view.ProductView;
import com.inspirationlogical.receipt.model.view.ProductViewImpl;

public class CommonServicesImpl implements CommonServices {

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
