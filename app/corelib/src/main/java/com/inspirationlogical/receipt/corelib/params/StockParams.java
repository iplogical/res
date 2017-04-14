package com.inspirationlogical.receipt.corelib.params;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import com.inspirationlogical.receipt.corelib.utility.Resources;
import lombok.Builder;
import lombok.Data;

/**
 * Created by TheDagi on 2017. 04. 14..
 */
@Builder
public @Data class StockParams {

    private String productName;

    private ReceiptType actionType;

    private double quantity;

    public enum StockActionType {
        PURCHASE(Resources.MANAGER.getString("Stock.Purchase")),
        INVENTORY(Resources.MANAGER.getString("Stock.Inventory")),
        DISPOSAL(Resources.MANAGER.getString("Stock.Disposal"));

        private String name;

        StockActionType(String name) {
            this.name = name;
        }
        @Override public String toString() {
            return name;
        }
    }
}
