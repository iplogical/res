package com.inspirationlogical.receipt.waiter.viewstate;

import lombok.Data;

public @Data class SaleViewState {

    private boolean takeAway;

    private boolean isGift;

    private CancellationType cancellationType;

    public boolean isSelectiveCancellation() {
        return cancellationType.equals(CancellationType.SELECTIVE);
    }

    public boolean isSingleCancellation() {
        return cancellationType.equals(CancellationType.SINGLE);
    }

    public enum CancellationType {
        NONE,
        SELECTIVE,
        SINGLE;
    }
}
