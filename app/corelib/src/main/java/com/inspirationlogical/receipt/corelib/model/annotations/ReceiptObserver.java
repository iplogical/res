package com.inspirationlogical.receipt.corelib.model.annotations;

import java.util.Collection;

public interface ReceiptObserver {
    static Collection<ReceiptObserver> observers = null;

    static Collection<ReceiptObserver> get() {
        return observers;
    }
    void onClose();
}
