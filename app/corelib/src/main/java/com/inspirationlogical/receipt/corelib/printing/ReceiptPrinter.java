package com.inspirationlogical.receipt.corelib.printing;

import com.inspirationlogical.receipt.corelib.params.ReceiptPrintModel;

public interface ReceiptPrinter {

    void printReceipt(ReceiptPrintModel receiptPrintModel);
}
