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
@ToString
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

//    @Override
//    public String getName() {
//        return adapter.getAdaptee().getShortName();
//    }
//
//    @Override
//    public int getOrderNumber() {
//        return adapter.getAdaptee().getOrderNumber();
//    }
//
//    @Override
//    public Long getId() {
//        return adapter.getAdaptee().getId();
//    }
//
//    @Override
//    public ProductType getType() {
//        return adapter.getAdaptee().getType();
//    }
//
//    @Override
//    public ProductStatus getStatus() {
//        return adapter.getAdaptee().getStatus();
//    }
//
//    @Override
//    public String getShortName() {
//        return adapter.getAdaptee().getShortName();
//    }
//
//    @Override
//    public String getLongName() {
//        return adapter.getAdaptee().getLongName();
//    }
//
//    @Override
//    public int getRapidCode() {
//        return adapter.getAdaptee().getRapidCode();
//    }
//
//    @Override
//    public QuantityUnit getQuantityUnit() {
//        return adapter.getAdaptee().getQuantityUnit();
//    }
//
//    @Override
//    public double getStorageMultiplier() {
//        return adapter.getAdaptee().getStorageMultiplier();
//    }
//
//    @Override
//    public int getPurchasePrice() {
//        return adapter.getAdaptee().getPurchasePrice();
//    }
//
//    @Override
//    public int getSalePrice() {
//        return adapter.getAdaptee().getSalePrice();
//    }
//
//    @Override
//    public double getVATLocal() {
//        return adapter.getAdaptee().getVATLocal();
//    }
//
//    @Override
//    public double getVATTakeAway() {
//        return adapter.getAdaptee().getVATTakeAway();
//    }
//
//    @Override
//    public int getMinimumStock() {
//        return adapter.getAdaptee().getMinimumStock();
//    }
//
//    @Override
//    public int getStockWindow() {
//        return adapter.getAdaptee().getStockWindow();
//    }
//
//    @Override
//    public List<RecipeView> getRecipes() {
//        return adapter.getAdaptee().getRecipes().stream()
//                .map(RecipeAdapter::new)
//                .map(RecipeViewImpl::new)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public String toString() {
//        return adapter.toString();
//    }
}
