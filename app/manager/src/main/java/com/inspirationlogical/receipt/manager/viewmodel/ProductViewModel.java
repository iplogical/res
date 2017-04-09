package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

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
    private String minimumStock;
    private String stockWindow;

    public ProductViewModel(ProductView productView) {
        type = productView != null ? productView.getType().name() : EMPTY;
        status = productView != null ? productView.getStatus().name() : EMPTY;
        shortName = productView != null ? productView.getShortName() : EMPTY;
        longName = productView != null ? productView.getLongName() : EMPTY;
        rapidCode = productView != null ? valueOf(productView.getRapidCode()) : EMPTY;
        quantityUnit = productView != null ? valueOf(productView.getQuantityUnit()) : EMPTY;
        quantityMultiplier = productView != null ? "0" : EMPTY;
        purchasePrice = productView != null ? valueOf(productView.getPurchasePrice()) : EMPTY;
        salePrice = productView != null ? valueOf(productView.getSalePrice()) : EMPTY;
        VATLocal = productView != null ? valueOf(productView.getVATLocal()) + PERCENT : EMPTY;
        VATTakeAway = productView != null ? valueOf(productView.getVATTakeAway()) + PERCENT : EMPTY;
        minimumStock = productView != null ? valueOf(productView.getMinimumStock()) : EMPTY;
        stockWindow = productView != null ? valueOf(productView.getStockWindow()) : EMPTY;
    }
}
