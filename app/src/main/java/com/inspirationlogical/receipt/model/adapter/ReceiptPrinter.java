package com.inspirationlogical.receipt.model.adapter;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class ReceiptPrinter implements ReceiptAdapter.Listener {

    @Override
    public void onOpen(ReceiptAdapter receipt) {
        // No-op
    }

    @Override
    public void onClose(ReceiptAdapter receipt) {
        // TODO: Generate XML from receipt
        // TODO: generate PDF from XML using XSLT
        // TODO: send pdf to print server
    }
}
