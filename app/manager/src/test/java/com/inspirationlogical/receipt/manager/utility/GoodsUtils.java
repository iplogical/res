package com.inspirationlogical.receipt.manager.utility;

import javafx.scene.input.KeyCode;

import static com.inspirationlogical.receipt.manager.utility.CategoryFormUtils.setCategoryName;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.manager.utility.ProductFormUtils.*;

public class GoodsUtils extends AbstractUtils {

    public static void enterGoodsView() {
        clickButtonThenWait("Common.GoodsView", 100);
    }

    public static void enterStockView() {
        clickButtonThenWait("Common.StockView", 100);
    }

     public static void enterPriceModifierView() {
        clickButtonThenWait("Common.PriceModifierView", 100);
    }

    public static void enterReceiptView() {
        clickButtonThenWait("Common.ReceiptsView", 100);
    }

    public static void clickOnShowDeleted() {
        clickButtonThenWait(SHOW_DELETED, 200);
    }

    public static void clickOnAddProduct() {
        clickButtonThenWait(ADD_PRODUCT, 500);
    }

    public static void clickOnModifyProduct() {
        clickButtonThenWait(MODIFY_PRODUCT, 500);
    }

    public static void clickOnDeleteProduct() {
        clickButtonThenWait(DELETE_PRODUCT, 500);
    }

    public static void clickOnAddCategory() {
        clickButtonThenWait(ADD_CATEGORY, 500);
    }

    public static void clickOnModifyCategory() {
        clickButtonThenWait(MODIFY_CATEGORY, 500);
    }

    public static void clickOnDeleteCategory() {
        clickButtonThenWait(DELETE_CATEGORY, 500);
    }

    public static void clickOnShowRecipeForm() {
        clickButtonThenWait(SHOW_RECIPE_FORM, 200);
    }

    public static void selectProduct(String longName) {
        clickOnThenWait(longName, 100);
    }

    public static void addProductWithDefaultParams(String longName, String shortName) {
        setLongName(longName);
        setShortName(shortName);
        setProductType(1);
        setCategory(1);
        setProductStatus(1);
        setRapidCode(100);
        setQuantityUnit(1);
        setStorageMultiplier(50);
        setSalePrice(500);
        setPurchasePrice(200);
        setMinimumStock(14);
        setStockWindow(60);
    }

    public static void modifyProduct(String longName, String newLongName, String newShortName) {
        selectProduct(longName);
        clickOnModifyProduct();
        setLongName(newLongName);
        setShortName(newShortName);
        clickOnConfirm();
    }

    public static void deleteProduct(String productLongName) {
        selectProduct(productLongName);
        clickOnDeleteProduct();
    }

    public static void modifyCategory(String name, String newName) {
        selectCategory(name);
        clickOnModifyCategory();
        setCategoryName(newName);
        clickOnConfirm();
    }

    public static void selectCategory(String name) {
        clickOnThenWait(name, 100);
    }
}
