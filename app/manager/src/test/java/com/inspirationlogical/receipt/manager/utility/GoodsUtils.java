package com.inspirationlogical.receipt.manager.utility;

import javafx.scene.input.KeyCode;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;

public class GoodsUtils extends AbstractUtils {

    public static void clickOnAddProduct() {
        clickButtonThenWait(ADD_PRODUCT, 500);
    }

    public static void selectCategory(int number) {
        selectChoiceBoxItem(PRODUCT_CATEGORY, number);
    }

    public static void selectProductType(int number) {
        selectChoiceBoxItem(PRODUCT_TYPE, number - 1);
    }

    public static void selectProductStatus(int number) {
        selectChoiceBoxItem(PRODUCT_STATUS, number - 1);
    }

    public static void selectQuantityUnit(int number) {
        selectChoiceBoxItem(PRODUCT_QUANTITY_UNIT, number);
    }

    private static void selectChoiceBoxItem(String fxId, int number) {
        clickOnThenWait(fxId, 100);
        for (int i = 0; i < number; i++) {
            robot.type(KeyCode.DOWN);
        }
        robot.type(KeyCode.ENTER);
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

    public static void addProductWithDefaultParams(String longName, String shortName) {
        setLongName(longName);
        setShortName(shortName);
        selectProductType(1);
        selectCategory(1);
        selectProductStatus(1);
        setRapidCode(100);
        selectQuantityUnit(1);
        setStorageMultiplier(50);
        setSalePrice(500);
        setPurchasePrice(200);
        setMinimumStock(14);
        setStockWindow(60);
    }
}
