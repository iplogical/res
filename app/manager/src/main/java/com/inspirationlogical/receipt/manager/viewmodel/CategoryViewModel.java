package com.inspirationlogical.receipt.manager.viewmodel;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.PSEUDO;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import java.util.List;

public class CategoryViewModel extends ProductViewModel {
    private static boolean hasProduct;
    private static ProductView productView;
    private String name;

    public CategoryViewModel(ProductCategoryView productCategoryView) {
        super(hasProduct(productCategoryView) ? productView : null);
        name = hasProduct ? getLongName() : productCategoryView.getName();
    }

    public CategoryViewModel(ProductView productView) {
        super(productView);
        name = productView.getName();
    }

    public String getName() {
        return name;
    }

    private static boolean hasProduct(ProductCategoryView productCategoryView) {
        if(productCategoryView.getType() != PSEUDO) {
            hasProduct = false;
            return false;
        }
        productView = productCategoryView.getProduct();
        hasProduct = productView != null;
        return hasProduct;
    }
}
