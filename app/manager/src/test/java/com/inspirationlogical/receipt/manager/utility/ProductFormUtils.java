package com.inspirationlogical.receipt.manager.utility;

import static com.inspirationlogical.receipt.manager.utility.ClickUtils.selectChoiceBoxItem;
import static com.inspirationlogical.receipt.manager.utility.ClickUtils.setTextField;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.*;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.PRODUCT_MINIMUM_STOCK;
import static com.inspirationlogical.receipt.manager.utility.JavaFXIds.PRODUCT_STOCK_WINDOW;

public class ProductFormUtils extends AbstractUtils {

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

}
