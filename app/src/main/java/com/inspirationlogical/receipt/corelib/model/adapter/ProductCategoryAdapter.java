package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.exception.RootCategoryNotFoundException;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.entity.Restaurant;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

import javax.persistence.EntityManager;

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
                .filter(elem -> !elem.getProduct().getType().equals(ProductType.AD_HOC_PRODUCT))
                .filter(elem -> elem.getProduct().getStatus().equals(ProductStatus.ACTIVE))
                .map(elem -> new ProductAdapter(elem.getProduct()))
                .collect(Collectors.toList());
    }
}
