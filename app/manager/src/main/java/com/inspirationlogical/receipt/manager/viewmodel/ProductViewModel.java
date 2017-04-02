package com.inspirationlogical.receipt.manager.viewmodel;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import lombok.Data;

@Data
public class ProductViewModel {
    private String type;
    private String status;
    private String shortName;
    private String longName;
    private String rapidCode;
    private String quantityUnit;
    private String quantityMultiplier;
    private String purchasePrice;
    private String salePrice;
    private String VATLocal;
    private String VATTakeAway;
    private String minimumStore;
    private String storeWindow;

    public ProductViewModel(ProductView productView) {
        this.type = productView.getType().name();
    }
}
