package com.inspirationlogical.receipt.manager.viewmodel;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.PSEUDO;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

public class CategoryViewModel extends ProductViewModel {
    private String name;

    public CategoryViewModel(ProductCategoryView productCategoryView) {
        super(hasProduct(productCategoryView) ? productCategoryView.getAllActiveProducts().get(0) : null);
        name = productCategoryView.getName();
    }

    public String getName() {
        return name;
    }

    private static boolean hasProduct(ProductCategoryView productCategoryView) {
        return productCategoryView.getType() == PSEUDO && !productCategoryView.getAllActiveProducts().isEmpty();
    }
}
