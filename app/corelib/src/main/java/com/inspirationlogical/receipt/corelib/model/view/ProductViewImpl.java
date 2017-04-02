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
        return adapter.getAdaptee().getType();
    }

    @Override
    public ProductStatus getStatus() {
        return adapter.getAdaptee().getStatus();
    }

    @Override
    public String getShortName() {
        return adapter.getAdaptee().getShortName();
    }

    @Override
    public String getLongName() {
        return adapter.getAdaptee().getLongName();
    }

    @Override
    public int getRapidCode() {
        return adapter.getAdaptee().getRapidCode();
    }

    @Override
    public QuantityUnit getQuantityUnit() {
        return adapter.getAdaptee().getQuantityUnit();
    }

    @Override
    public EtalonQuantity getEtalonQuantity() {
        return adapter.getAdaptee().getEtalonQuantity();
    }

    @Override
    public double getQuantityMultiplier() {
        return adapter.getAdaptee().getQuantityMultiplier();
    }

    @Override
    public int getPurchasePrice() {
        return adapter.getAdaptee().getPurchasePrice();
    }

    @Override
    public int getSalePrice() {
        return adapter.getAdaptee().getSalePrice();
    }

    @Override
    public double getVATLocal() {
        return adapter.getAdaptee().getVATLocal();
    }

    @Override
    public double getVATTakeAway() {
        return adapter.getAdaptee().getVATTakeAway();
    }

    @Override
    public int getMinimumStore() {
        return adapter.getAdaptee().getMinimumStore();
    }

    @Override
    public int getStoreWindow() {
        return adapter.getAdaptee().getStoreWindow();
    }
}
