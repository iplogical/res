package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.PriceModifierAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.view.*;

public class CommonServiceImpl extends AbstractService implements CommonService {

    @Inject
    public CommonServiceImpl(EntityManager manager) {
        super(manager);
    }

//    public static <V, A> List<V> createViewsFromAdapters(List<A> adapters, Class<V> viewImpl) {
//        return adapters.stream()
//                .map(viewImpl::new)
//                .collect(Collectors.toList());
//    }

    public static List<ProductView> createProductViews(List<ProductAdapter> adapters) {
        return adapters.stream()
                .map(productAdapter -> new ProductViewImpl(productAdapter))
                .collect(Collectors.toList());
    }

    public static List<StockView> createStockViews(List<StockAdapter> adapters) {
        return adapters.stream()
                .map(stockAdapter -> new StockViewImpl(stockAdapter))
                .collect(Collectors.toList());
    }

    public static List<PriceModifierView> createPriceModifierViews(List<PriceModifierAdapter> adapters) {
        return adapters.stream()
                .map(priceModifierAdapter -> new PriceModifierViewImpl(priceModifierAdapter))
                .collect(Collectors.toList());
    }

    @Override
    public ProductCategoryView getRootProductCategory() {
        return new ProductCategoryViewImpl(ProductCategoryAdapter.getRootCategory(manager));
    }

    @Override
    public List<ProductView> getProducts(ProductCategoryView category) {
        return createProductViews(getProductCategoryAdapter(category).getAllProducts());
    }

    @Override
    public List<StockView> getStockItems() {
        return createStockViews(StockAdapter.getItems());
    }

    @Override
    public List<PriceModifierView> getPriceModifiers() {
        return createPriceModifierViews(PriceModifierAdapter.getPriceModifiers());
    }
}
