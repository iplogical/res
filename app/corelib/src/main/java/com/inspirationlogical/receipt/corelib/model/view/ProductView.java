package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;

public interface ProductView extends AbstractView {

    ProductType getType();

    ProductStatus getStatus();

    String getShortName();

    String getLongName();

    int getRapidCode();

    QuantityUnit getQuantityUnit();

    double getQuantityMultiplier();

    int getPurchasePrice();

    int getSalePrice();

    double getVATLocal();

    double getVATTakeAway();

    int getMinimumStock();

    int getStockWindow();
}
