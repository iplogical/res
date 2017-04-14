package com.inspirationlogical.receipt.corelib.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.inspirationlogical.receipt.corelib.model.adapter.PriceModifierAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.ProductCategoryAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.StockAdapter;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.*;
import com.inspirationlogical.receipt.corelib.params.PriceModifierParams;
import com.inspirationlogical.receipt.corelib.params.ProductCategoryParams;
import com.inspirationlogical.receipt.corelib.params.StockParams;

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
        return adapters.stream().map(ProductViewImpl::new).collect(Collectors.toList());
    }

    public static List<StockView> createStockViews(List<StockAdapter> adapters) {
        return adapters.stream().map(StockViewImpl::new).collect(Collectors.toList());
    }

    public static List<PriceModifierView> createPriceModifierViews(List<PriceModifierAdapter> adapters) {
        return adapters.stream().map(PriceModifierViewImpl::new).collect(Collectors.toList());
    }

    @Override
    public Product.ProductBuilder productBuilder() {
        return Product.builder();
    }

    @Override
    public PriceModifier.PriceModifierBuilder priceModifierBuilder() {
        return PriceModifier.builder();
    }

    @Override
    public ProductView addProduct(ProductCategoryView parent, Product.ProductBuilder builder) {
        return new ProductViewImpl(getProductCategoryAdapter(parent).addProduct(builder));
    }

    @Override
    public ProductCategoryView addProductCategory(ProductCategoryParams params) {
        return new ProductCategoryViewImpl(getProductCategoryAdapter(params.getParent())
                .addChildCategory(params.getName(), params.getType()));
    }

    @Override
    public ProductCategoryView updateProductCategory(ProductCategoryParams params) {
        return new ProductCategoryViewImpl(getProductCategoryAdapter(params.getParent())
                .updateChildCategory(params.getName(), params.getOriginalName(), params.getType()));
    }

    @Override
    public void updateStock(List<StockParams> params) {
        params.forEach(params1 -> System.out.println(params1));
    }

    @Override
    public void addPriceModifier(PriceModifierParams params) {
        PriceModifierAdapter.addPriceModifier(params);
    }

    @Override
    public ProductCategoryView getRootProductCategory() {
        return new ProductCategoryViewImpl(ProductCategoryAdapter.getRootCategory());
    }

    @Override
    public List<ProductCategoryView> getAllCategories() {
        List<ProductCategoryView> categoryViews = ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.ROOT).stream().map(ProductCategoryViewImpl::new)
                .collect(Collectors.toList());
        categoryViews.addAll(ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.AGGREGATE).stream().map(ProductCategoryViewImpl::new)
                .collect(Collectors.toList()));
        categoryViews.addAll(ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.LEAF).stream().map(ProductCategoryViewImpl::new)
                .collect(Collectors.toList()));
        return categoryViews;
    }

    @Override
    public List<ProductCategoryView> getAggregateCategories() {
        return ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.AGGREGATE).stream().map(ProductCategoryViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductCategoryView> getLeafCategories() {
        return ProductCategoryAdapter.getCategoriesByType(ProductCategoryType.LEAF).stream().map(ProductCategoryViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductView> getSellableProducts(ProductCategoryView category) {
        return createProductViews(getProductCategoryAdapter(category).getAllSellableProducts());
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
