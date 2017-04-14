package com.inspirationlogical.receipt.manager.viewmodel;

import static java.lang.String.valueOf;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.inspirationlogical.receipt.corelib.model.view.ProductView;
import com.inspirationlogical.receipt.corelib.model.view.RecipeView;

import lombok.Data;

@Data
public class ProductViewModel {

    private static String PERCENT = " %";

    private String type = EMPTY;
    private String status = EMPTY;
    private String shortName = EMPTY;
    private String longName = EMPTY;
    private String rapidCode = EMPTY;
    private String quantityUnit = EMPTY;
    private String quantityMultiplier = EMPTY;
    protected String storageMultiplier = EMPTY;
    private String purchasePrice = EMPTY;
    private String salePrice = EMPTY;
    private String VATLocal = EMPTY;
    private String VATTakeAway = EMPTY;
    private String minimumStock = EMPTY;
    private String stockWindow = EMPTY;

    private List<RecipeView> recipes = new ArrayList<>();

    public ProductViewModel(ProductView productView) {
        if (productView != null) {
            type = productView.getType().name();
            status =  productView.getStatus().name();
            shortName =  productView.getShortName();
            longName =  productView.getLongName();
            rapidCode =  valueOf(productView.getRapidCode());
            quantityUnit =  valueOf(productView.getQuantityUnit());
            storageMultiplier = valueOf(productView.getStorageMultiplier());
            purchasePrice =  valueOf(productView.getPurchasePrice());
            salePrice =  valueOf(productView.getSalePrice());
            VATLocal =  valueOf(productView.getVATLocal()) + PERCENT;
            VATTakeAway =  valueOf(productView.getVATTakeAway()) + PERCENT;
            minimumStock =  valueOf(productView.getMinimumStock());
            stockWindow =  valueOf(productView.getStockWindow());

            recipes = productView.getRecipes();

            Optional<RecipeView> recipeView = productView.getRecipes()
                    .stream()
                    .filter(RecipeView::isTrivial).findFirst();

            if (recipeView.isPresent()) {
                quantityMultiplier = valueOf(recipeView.get().getQuantity());
                recipes.clear();
            }
        }
    }
}
