package com.inspirationlogical.receipt.manager.viewmodel;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.PSEUDO;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;

public class CategoryViewModel extends ProductViewModel {
    private String name;

    public CategoryViewModel(ProductCategoryView productCategoryView) {
        super(hasProduct(productCategoryView) ? productCategoryView.getAllActiveProducts().get(0) : null);
        name = hasProduct(productCategoryView) ? null : productCategoryView.getName();
    }

    public CategoryViewModel(ProductView productView) {
        super(productView);
        name = EMPTY;
    }

    public String getName() {
        return name;
    }

    private static boolean hasProduct(ProductCategoryView productCategoryView) {
        return productCategoryView.getType() == PSEUDO && !productCategoryView.getAllActiveProducts().isEmpty();
    }
}
