package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.entity.ProductCategory;
import com.inspirationlogical.receipt.corelib.model.enums.*;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryFamily.FOOD;
import static com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryFamily.initFamily;

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
    private ProductCategoryFamily family;

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
        family = initFamily(product);

    }

    private List<RecipeView> initRecipeViews(Product product) {
        return product.getRecipes().stream()
                .map(RecipeView::new)
                .collect(Collectors.toList());
    }
}
