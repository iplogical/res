package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.PriceModifierStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import static java.util.stream.Collectors.toList;

public class ProductCategoryAdapter extends AbstractAdapter<ProductCategory>
{

    public ProductCategoryAdapter(ProductCategory adaptee) {
        super(adaptee);
    }

    public static ProductCategoryAdapter getRootCategory(EntityManager manager) {
        List<ProductCategory> rootCategoryList = manager.createNamedQuery(ProductCategory.GET_CATEGORY_BY_TYPE)
                .setParameter("type", ProductCategoryType.ROOT)
                .getResultList();
        if(rootCategoryList.isEmpty()) {
            throw new RootCategoryNotFoundException();
        }
        return new ProductCategoryAdapter(rootCategoryList.get(0));
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

    public List<ProductAdapter> getAllActiveProducts() {
        return getAllProducts().stream()
                .filter(productAdapter -> productAdapter.getAdaptee().getStatus().equals(ProductStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    public List<ProductAdapter> getAllNormalProducts() {
        return getAllActiveProducts().stream()
                .filter(productAdapter -> !productAdapter.getAdaptee().getType().equals(ProductType.AD_HOC_PRODUCT))
                .filter(productAdapter -> !productAdapter.getAdaptee().getType().equals(ProductType.GAME_FEE_PRODUCT))
                .filter(productAdapter -> !productAdapter.getAdaptee().getType().equals(ProductType.STORABLE))
                .collect(Collectors.toList());
    }

    public Collection<ProductCategoryAdapter> getChildrenCategories() {
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        return adaptee.getChildren().stream()
                .map(elem -> new ProductCategoryAdapter(elem))
                .filter(productCategoryAdapter -> !productCategoryAdapter.getAllNormalProducts().isEmpty())
                .collect(toList());
    }

    public ProductCategoryAdapter getParent() {
        return new ProductCategoryAdapter(adaptee.getParent());
    }

    public ProductCategoryType getType() {
        return adaptee.getType();
    }

    public double getDiscount(ReceiptRecordAdapter receiptRecordAdapter) {
        // TODO: PriceModifierStatus should be calculated or the start and end date should be used in the filter.
        GuardedTransaction.RunWithRefresh(adaptee, () -> {});
        ProductCategory category = adaptee;
        List<PriceModifierAdapter> priceModifiers = new ArrayList<>();
        do {
            List<PriceModifierAdapter> loopPriceModifiers = category.getPriceModifier().stream()
                    .filter(priceModifier -> priceModifier.getOwner().equals(adaptee))
                    .filter(priceModifier -> priceModifier.getStatus().equals(PriceModifierStatus.ACTUAL))
                    .map(priceModifier -> new PriceModifierAdapter(priceModifier))
                    .collect(toList());
            category = category.getParent();
            priceModifiers.addAll(loopPriceModifiers);
        } while(!category.getType().equals(ProductCategoryType.ROOT));

        return priceModifiers.stream().map(pm -> pm.getDiscountPercent(receiptRecordAdapter))
                .max(Double::compareTo).orElse(0D);
    }
}
