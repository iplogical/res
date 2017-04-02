package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;

import lombok.Data;

@Data
public class ProductViewModel {

    private static String PERCENT = " %";

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
        type = productView.getType().name();
        status = productView.getStatus().name();
        shortName = productView.getShortName();
        longName = productView.getLongName();
        rapidCode = valueOf(productView.getRapidCode());
        quantityUnit = valueOf(productView.getQuantityUnit());
        quantityMultiplier = valueOf(productView.getQuantityMultiplier());
        purchasePrice = valueOf(productView.getPurchasePrice());
        salePrice = valueOf(productView.getSalePrice());
        VATLocal = valueOf(productView.getVATLocal()) + PERCENT;
        VATTakeAway = valueOf(productView.getVATTakeAway()) + PERCENT;
        minimumStore = valueOf(productView.getMinimumStore());
        storeWindow = valueOf(productView.getStoreWindow());
    }
}
