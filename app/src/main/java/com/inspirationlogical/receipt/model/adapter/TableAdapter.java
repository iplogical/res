package com.inspirationlogical.receipt.model.adapter;

import com.inspirationlogical.receipt.model.Table;

public interface TableAdapter extends AbstractAdapter<Table> {

    public ReceiptAdapter getActiveReceipt();

}
