package com.inspirationlogical.receipt.model.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.ProductCategory;
import com.inspirationlogical.receipt.model.enums.ProductCategoryType;

public class ProductCategoryAdapterImpl extends AbstractAdapterImpl<ProductCategory>
    implements ProductCategoryAdapter {

    public ProductCategoryAdapterImpl(ProductCategory adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    public static void traverseChildren(ProductCategory current, List<ProductCategory> traversal) {
        traversal.add(current);
        if(current.getChildren() == null) return;
        current.getChildren().forEach((child) -> {
            traverseChildren(child, traversal);
        });
    }

    @Override
    public List<ProductAdapter> getAllProducts() {
        List<ProductCategory> childCategories = new ArrayList<>();
        List<ProductAdapter> productList = new ArrayList<>();

        traverseChildren(this.adaptee, childCategories);

        childCategories.forEach(category -> {
            if(category.getType().equals(ProductCategoryType.PSEUDO)) {
                productList.add(new ProductAdapterImpl(category.getProduct(), manager));
            }
        });
        return productList;
    }

}
