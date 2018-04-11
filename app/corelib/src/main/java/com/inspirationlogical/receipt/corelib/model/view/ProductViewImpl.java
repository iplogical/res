package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.entity.Product;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import com.inspirationlogical.receipt.corelib.model.enums.QuantityUnit;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ProductViewImpl implements ProductView {

    private long id;
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
    private double VATLocal;
    private double VATTakeAway;
    private int minimumStock;
    private int stockWindow;
    private List<RecipeView> recipes;

    public ProductViewImpl(Product product) {
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
        VATLocal = product.getVATLocal();
        VATTakeAway = product.getVATTakeAway();
        minimumStock = product.getMinimumStock();
        stockWindow = product.getStockWindow();
        recipes = initRecipeViews(product);
    }

    private List<RecipeView> initRecipeViews(Product product) {
        return product.getRecipes().stream()
                .map(RecipeViewImpl::new)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ProductViewImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", orderNumber=" + orderNumber +
                ", type=" + type +
                ", status=" + status +
                ", shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", rapidCode=" + rapidCode +
                ", quantityUnit=" + quantityUnit +
                ", storageMultiplier=" + storageMultiplier +
                ", purchasePrice=" + purchasePrice +
                ", salePrice=" + salePrice +
                ", VATLocal=" + VATLocal +
                ", VATTakeAway=" + VATTakeAway +
                ", minimumStock=" + minimumStock +
                ", stockWindow=" + stockWindow +
                ", recipes=" + recipes +
                '}';
    }
}
