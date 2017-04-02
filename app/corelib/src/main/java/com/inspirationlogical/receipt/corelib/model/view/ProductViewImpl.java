package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.enums.EtalonQuantity;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;

public class ProductViewImpl extends AbstractModelViewImpl<ProductAdapter>
        implements ProductView {

    public ProductViewImpl(ProductAdapter adapter) {
        super(adapter);
    }

    @Override
    public String getName() {
        return adapter.getAdaptee().getShortName();
    }

    @Override
    public ProductType getType() {
        return null;
    }

    @Override
    public ProductStatus getStatus() {
        return null;
    }

    @Override
    public String getShortName() {
        return null;
    }

    @Override
    public String getLongName() {
        return null;
    }

    @Override
    public int getRapidCode() {
        return 0;
    }

    @Override
    public QuantityUnit getQuantityUnit() {
        return null;
    }

    @Override
    public EtalonQuantity getEtalonQuantity() {
        return null;
    }

    @Override
    public double getQuantityMultiplier() {
        return 0;
    }

    @Override
    public int getPurchasePrice() {
        return 0;
    }

    @Override
    public int getSalePrice() {
        return 0;
    }

    @Override
    public double getVATTakeAway() {
        return 0;
    }

    @Override
    public int getMinimumStore() {
        return 0;
    }

    @Override
    public int getStoreWindow() {
        return 0;
    }
}
