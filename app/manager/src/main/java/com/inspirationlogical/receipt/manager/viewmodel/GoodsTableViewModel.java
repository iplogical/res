package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;

import lombok.Data;

@Data
public class GoodsTableViewModel {

    private static String PERCENT = " %";

    private String name = EMPTY;
    private int id;
    private String type = EMPTY;
    private String status = EMPTY;
    private String shortName = EMPTY;
    private String rapidCode = EMPTY;
    private String quantityUnit = EMPTY;
    private String quantityMultiplier = EMPTY;
    protected String storageMultiplier = EMPTY;
    private String purchasePrice = EMPTY;
    private String salePrice = EMPTY;
    private String minimumStock = EMPTY;
    private String stockWindow = EMPTY;
    private String orderNumber = EMPTY;

    public GoodsTableViewModel() {
    }

    public GoodsTableViewModel(ProductView productView) {
        id = productView.getId();
        type = productView.getType().toI18nString();
        status =  productView.getStatus().toI18nString();
        shortName =  productView.getShortName();
        name =  productView.getLongName();
        rapidCode =  valueOf(productView.getRapidCode());
        quantityUnit =  valueOf(productView.getQuantityUnit().toI18nString());
        storageMultiplier = valueOf(productView.getStorageMultiplier());
        purchasePrice =  valueOf(productView.getPurchasePrice());
        salePrice =  valueOf(productView.getSalePrice());
        minimumStock =  valueOf(productView.getMinimumStock());
        stockWindow =  valueOf(productView.getStockWindow());
        orderNumber = valueOf(productView.getOrderNumber());

        Optional<RecipeView> recipeView = productView.getRecipes().stream()
                .filter(RecipeView::isTrivial).findFirst();

        recipeView.ifPresent(recipe -> quantityMultiplier = valueOf(recipe.getQuantity()));
    }

    public boolean isProduct() {
        return !shortName.equals("");
    }

    public boolean isCategory() {
        return !isProduct();
    }

    public boolean isSellable() {
        return !type.equals(ProductType.STORABLE.toI18nString());

    }
}
