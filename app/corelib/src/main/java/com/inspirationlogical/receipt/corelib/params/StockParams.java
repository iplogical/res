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

    private double quantity;

    private boolean isAbsoluteQuantity;
}
