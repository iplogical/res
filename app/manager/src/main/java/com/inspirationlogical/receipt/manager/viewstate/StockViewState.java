package com.inspirationlogical.receipt.manager.viewstate;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptType;
import javafx.beans.property.BooleanProperty;
import lombok.Data;

/**
 * Created by TheDagi on 2017. 04. 14..
 */
public @Data class StockViewState {
    private ReceiptType receiptType;

    private BooleanProperty isAbsoluteQuantity;
}
