package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

import lombok.Data;

@Data
public class CategoryViewModel {
    private String name;
    private ProductViewModel productViewModel;

    public CategoryViewModel(ProductCategoryView productCategoryView) {
        this.name = productCategoryView.getName();
        if (productCategoryView.getType() == ProductCategoryType.PSEUDO) {
            productViewModel = new ProductViewModel(productCategoryView.getAllProducts().get(0));
        } else {
            productViewModel = null;
        }
    }

    public boolean hasProduct() {
        return productViewModel != null;
    }
}
