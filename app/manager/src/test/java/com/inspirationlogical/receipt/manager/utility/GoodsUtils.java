package com.inspirationlogical.receipt.manager.utility;

import javafx.scene.input.KeyCode;

import static com.inspirationlogical.receipt.manager.utility.CategoryFormUtils.setCategoryName;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;
import static java.lang.Thread.sleep;

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

    public static void setCategory(int number) {
        selectChoiceBoxItem(PRODUCT_CATEGORY, number);
    }

    public static void setProductType(int number) {
        selectChoiceBoxItem(PRODUCT_TYPE, number - 1);
    }

    public static void setProductStatus(int number) {
        selectChoiceBoxItem(PRODUCT_STATUS, number - 1);
    }

    public static void setQuantityUnit(int number) {
        selectChoiceBoxItem(PRODUCT_QUANTITY_UNIT, number);
    }

    public static void setLongName(String longName) {
        setTextField(PRODUCT_LONG_NAME, longName);
    }

    public static void setShortName(String  shortName) {
        setTextField(PRODUCT_SHORT_NAME, shortName);
    }

    public static void setRapidCode(int rapidCode) {
        setTextField(PRODUCT_RAPID_CODE, String.valueOf(rapidCode));
    }

    public static void setStorageMultiplier(int storageMultiplier) {
        setTextField(PRODUCT_STORAGE_MULTIPLIER, String.valueOf(storageMultiplier));
    }

    public static void setSalePrice(int salePrice) {
        setTextField(PRODUCT_SALE_PRICE, String.valueOf(salePrice));
    }

    public static void setPurchasePrice(int purchasePrice) {
        setTextField(PRODUCT_PURCHASE_PRICE, String.valueOf(purchasePrice));
    }

    public static void setMinimumStock(int minimumStock) {
        setTextField(PRODUCT_MINIMUM_STOCK, String.valueOf(minimumStock));
    }

    public static void setStockWindow(int stockWindow) {
        setTextField(PRODUCT_STOCK_WINDOW, String.valueOf(stockWindow));
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
