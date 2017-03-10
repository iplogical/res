package com.inspirationlogical.receipt.model.annotations;

import java.util.Collection;

public interface ReceiptObserver {
    static Collection<ReceiptObserver> observers = null;

    static Collection<ReceiptObserver> get() {
        return observers;
    }
    void onClose();
}
