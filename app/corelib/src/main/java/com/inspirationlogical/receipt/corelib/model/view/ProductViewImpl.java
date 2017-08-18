package com.inspirationlogical.receipt.corelib.model.view;

import java.util.List;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.adapter.ProductAdapter;
import com.inspirationlogical.receipt.corelib.model.adapter.RecipeAdapter;
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
    public int getOrderNumber() {
        return adapter.getAdaptee().getOrderNumber();
    }

    @Override
    public Long getId() {
        return adapter.getAdaptee().getId();
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
    public double getStorageMultiplier() {
        return adapter.getAdaptee().getStorageMultiplier();
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
    public int getMinimumStock() {
        return adapter.getAdaptee().getMinimumStock();
    }

    @Override
    public int getStockWindow() {
        return adapter.getAdaptee().getStockWindow();
    }

    @Override
    public List<RecipeView> getRecipes() {
        return adapter.getAdaptee().getRecipes().stream()
                .map(RecipeAdapter::new)
                .map(RecipeViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return adapter.toString();
    }
}
