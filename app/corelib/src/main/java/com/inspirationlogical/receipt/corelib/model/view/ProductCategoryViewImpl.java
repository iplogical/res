package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;

public class ProductCategoryViewImpl extends AbstractModelViewImpl<ProductCategoryAdapter>
        implements ProductCategoryView {

    public ProductCategoryViewImpl(ProductCategoryAdapter adapter) {
        super(adapter);
    }


    @Override
    public String getCategoryName() {
        return adapter.getAdaptee().getName();
    }

    @Override
    public ProductCategoryView getParent() {
        return new ProductCategoryViewImpl(adapter.getParent());
    }

    @Override
    public List<ProductCategoryView> getChildrenCategories() {
        return adapter.getChildrenCategories().stream()
                .map(category -> new ProductCategoryViewImpl(category))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductView> getAllProducts() {
        return mapProducts(adapter.getAllProducts());
    }

    @Override
    public List<ProductView> getAllActiveProducts() {
        return mapProducts(adapter.getAllActiveProducts());
    }

    @Override
    public List<ProductView> getAllNormalProducts() {
        return mapProducts(adapter.getAllSellableProducts());
    }

    @Override
    public ProductCategoryType getType() {
        return adapter.getType();
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getName();
    }

    private List<ProductView> mapProducts(List<ProductAdapter> productAdapters) {
        return productAdapters.stream()
                .map(productAdapter -> new ProductViewImpl(productAdapter))
                .collect(Collectors.toList());
    }
}
