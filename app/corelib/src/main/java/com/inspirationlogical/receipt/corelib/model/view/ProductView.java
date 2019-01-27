package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString(exclude = "recipes")
public class ProductView implements AbstractView {

    private int id;
    private String name;
    private int orderNumber;
    private ProductType type;
    private ProductStatus status;
    private String shortName;
    private String longName;
    private int rapidCode;
    private QuantityUnit quantityUnit;
    private double storageMultiplier;
    private int purchasePrice;
    private int salePrice;
    private int minimumStock;
    private int stockWindow;
    private List<RecipeView> recipes;

    public ProductView(Product product) {
        id = product.getId();
        name = product.getShortName();
        orderNumber = product.getOrderNumber();
        type = product.getType();
        status = product.getStatus();
        shortName = product.getShortName();
        longName = product.getLongName();
        rapidCode = product.getRapidCode();
        quantityUnit = product.getQuantityUnit();
        storageMultiplier = product.getStorageMultiplier();
        purchasePrice = product.getPurchasePrice();
        salePrice = product.getSalePrice();
        minimumStock = product.getMinimumStock();
        stockWindow = product.getStockWindow();
        recipes = initRecipeViews(product);
    }

    private List<RecipeView> initRecipeViews(Product product) {
        return product.getRecipes().stream()
                .map(RecipeView::new)
                .collect(Collectors.toList());
    }
}
