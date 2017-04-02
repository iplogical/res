package com.inspirationlogical.receipt.manager.viewmodel;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType.PSEUDO;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;

public class CategoryViewModel {
    private String name;
    private ProductViewModel productViewModel;

    public CategoryViewModel(ProductCategoryView productCategoryView) {
        this.name = productCategoryView.getName();
        if (productCategoryView.getType() == PSEUDO && !productCategoryView.getAllActiveProducts().isEmpty()) {
            productViewModel = new ProductViewModel(productCategoryView.getAllActiveProducts().get(0));
        } else {
            productViewModel = null;
        }
    }

    public boolean hasProduct() {
        return productViewModel != null;
    }

    public String getName() {
        return !hasProduct() ? name : EMPTY;
    }

    public String getType() {
        return hasProduct() ? productViewModel.getType() : EMPTY;
    }

    public String getStatus() {
        return hasProduct() ? productViewModel.getStatus() : EMPTY;
    }

    public String getShortName() {
        return hasProduct() ? productViewModel.getShortName() : EMPTY;
    }

    public String getLongName() {
        return hasProduct() ? productViewModel.getLongName() : EMPTY;
    }

    public String getRapidCode() {
        return hasProduct() ? productViewModel.getRapidCode() : EMPTY;
    }

    public String getQuantityUnit() {
        return hasProduct() ? productViewModel.getQuantityUnit() : EMPTY;
    }

    public String getQuantityMultiplier() {
        return hasProduct() ? productViewModel.getQuantityMultiplier() : EMPTY;
    }

    public String getPurchasePrice() {
        return hasProduct() ? productViewModel.getPurchasePrice() : EMPTY;
    }

    public String getSalePrice() {
        return hasProduct() ? productViewModel.getSalePrice() : EMPTY;
    }

    public String getVATLocal() {
        return hasProduct() ? productViewModel.getVATLocal() : EMPTY;
    }

    public String getVATTakeAway() {
        return hasProduct() ? productViewModel.getVATTakeAway() : EMPTY;
    }

    public String getMinimumStore() {
        return hasProduct() ? productViewModel.getMinimumStore() : EMPTY;
    }

    public String getStoreWindow() {
        return hasProduct() ? productViewModel.getStoreWindow() : EMPTY;
    }
}
