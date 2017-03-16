package com.inspirationlogical.receipt.corelib.model.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.utils.GuardedTransaction;

public class ProductCategoryAdapter extends AbstractAdapter<ProductCategory>
{

    public ProductCategoryAdapter(ProductCategory adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    public static void traverseChildren(ProductCategory current, List<ProductCategory> traversal) {
        traversal.add(current);
        if(current.getChildren() == null) return;
        current.getChildren().forEach((child) -> {
            traverseChildren(child, traversal);
        });
    }

    public List<ProductAdapter> getAllProducts() {
        GuardedTransaction.Run(manager,() -> manager.refresh(adaptee));
        List<ProductCategory> childCategories = new ArrayList<>();
        traverseChildren(this.adaptee, childCategories);
        return childCategories.stream()
                .filter(elem -> elem.getType().equals(ProductCategoryType.PSEUDO))
                .map(elem -> new ProductAdapter(elem.getProduct(),manager))
                .collect(Collectors.toList());
    }

}