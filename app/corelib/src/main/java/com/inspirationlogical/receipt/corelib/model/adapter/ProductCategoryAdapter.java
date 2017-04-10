package com.inspirationlogical.receipt.corelib.model.adapter;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.*;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.exception.IllegalProductCategoryStateException;
import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;
import com.inspirationlogical.receipt.corelib.utility.Resources;

public class ProductCategoryAdapter extends AbstractAdapter<ProductCategory>
{

    public ProductCategoryAdapter(ProductCategory adaptee) {
        super(adaptee);
    }

    public static ProductCategoryAdapter getRootCategory() {
        List<ProductCategory> rootCategoryList = GuardedTransaction.RunNamedQuery(ProductCategory.GET_CATEGORY_BY_TYPE,
                query -> {query.setParameter("type", ProductCategoryType.ROOT);
                    return query;});
        if(rootCategoryList.isEmpty()) {
            throw new RootCategoryNotFoundException();
        }
        return new ProductCategoryAdapter(rootCategoryList.get(0));
    }

    public static List<ProductCategoryAdapter> getAggregateCategories() {
        List<ProductCategory> aggregates = GuardedTransaction.RunNamedQuery(ProductCategory.GET_CATEGORY_BY_TYPE,
                query -> {query.setParameter("type", ProductCategoryType.AGGREGATE);
                    return query;});
        return aggregates.stream().map(ProductCategoryAdapter::new)
                .collect(toList());
    }

    public static void traverseChildren(ProductCategory current, List<ProductCategory> traversal) {
        traversal.add(current);
        if(current.getChildren() == null) return;
        current.getChildren().forEach((child) -> {
            traverseChildren(child, traversal);
        });
    }

    public List<ProductAdapter> getAllProducts() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        List<ProductCategory> childCategories = new ArrayList<>();
        traverseChildren(this.adaptee, childCategories);
        return childCategories.stream()
                .filter(elem -> elem.getType().equals(ProductCategoryType.PSEUDO))
                .map(elem -> new ProductAdapter(elem.getProduct()))
                .collect(toList());
    }

    public List<ProductCategoryAdapter> getAllProductCategories() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        List<ProductCategory> childCategories = new ArrayList<>();
        traverseChildren(this.adaptee, childCategories);
        return childCategories.stream()
                .filter(elem -> !elem.getType().equals(ProductCategoryType.PSEUDO))
                .map(elem -> new ProductCategoryAdapter(elem))
                .collect(toList());
    }

    public List<ProductAdapter> getAllActiveProducts() {
        return getAllProducts().stream()
                .filter(productAdapter -> productAdapter.getAdaptee().getStatus().equals(ProductStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    public List<ProductAdapter> getAllSellableProducts() {
        return getAllActiveProducts().stream()
                .filter(productAdapter -> !productAdapter.getAdaptee().getType().equals(ProductType.AD_HOC_PRODUCT))
                .filter(productAdapter -> !productAdapter.getAdaptee().getType().equals(ProductType.GAME_FEE_PRODUCT))
                .filter(productAdapter -> !productAdapter.getAdaptee().getType().equals(ProductType.STORABLE))
                .collect(Collectors.toList());
    }

    public Set<ProductAdapter> getAllStorableProducts() {
         return getAllActiveProducts().stream()
                .flatMap(productAdapter -> productAdapter.getAdaptee().getRecipe().stream())
                .map(recipe -> new ProductAdapter(recipe.getElement()))
                .collect(toSet());
    }

    public Collection<ProductCategoryAdapter> getChildrenCategories() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        return adaptee.getChildren().stream()
                .map(elem -> new ProductCategoryAdapter(elem))
                .collect(toList());
    }

    public ProductCategoryAdapter getParent() {
        return new ProductCategoryAdapter(adaptee.getParent());
    }

    public ProductCategoryType getType() {
        return adaptee.getType();
    }

    public double getDiscount(ReceiptRecordAdapter receiptRecordAdapter) {
        // TODO: Implement filter for DAILY and WEEKLY repeating PriceModifiers.
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        ProductCategory category = adaptee;
        List<PriceModifierAdapter> priceModifiers = new ArrayList<>();
        do {
            List<PriceModifierAdapter> loopPriceModifiers = category.getPriceModifier().stream()
                    .filter(priceModifier -> priceModifier.getOwner().equals(adaptee))
                    .filter(priceModifier -> priceModifier.getStartTime().isBefore(now()))
                    .filter(priceModifier -> priceModifier.getEndTime().isAfter(now()))
                    .map(priceModifier -> new PriceModifierAdapter(priceModifier))
                    .collect(toList());
            category = category.getParent();
            priceModifiers.addAll(loopPriceModifiers);
        } while(!category.getType().equals(ProductCategoryType.ROOT));

        return priceModifiers.stream().map(pm -> pm.getDiscountPercent(receiptRecordAdapter))
                .max(Double::compareTo).orElse(0D);
    }

    public ProductCategoryAdapter addChildCategory(String name, ProductCategoryType type) {
        final ProductCategory[] newCategory = new ProductCategory[1];
        GuardedTransaction.RunWithRefresh(adaptee, () -> {
            newCategory[0] = ProductCategory.builder()
                    .name(name)
                    .type(type)
                    .parent(adaptee)
                    .build();
            getRootCategory().getAllProductCategories().stream()
                    .filter(productCategoryAdapter -> productCategoryAdapter.getAdaptee().getName().equals(name))
                    .map(category -> {throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryNameAlreadyUsed") + name);})
                    .collect(Collectors.toList());
            if(type.equals(ProductCategoryType.AGGREGATE)) {
                adaptee.getChildren().stream()
                        .filter(productCategory -> productCategory.getType().equals(ProductCategoryType.LEAF))
                        .map(productCategory -> {throw new IllegalProductCategoryStateException(Resources.CONFIG.getString("ProductCategoryAlreadyHasLeaf"));})
                        .collect(Collectors.toList());
            }
            adaptee.getChildren().add(newCategory[0]);
        });
        return new ProductCategoryAdapter(newCategory[0]);
    }
}
