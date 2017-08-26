package com.inspirationlogical.receipt.waiter.controller.reatail.sale;

import lombok.Data;

@Data
class SaleViewState {

    private boolean takeAway;

    private boolean gift;

    private CancellationType cancellationType;

    boolean isSelectiveCancellation() {
        return cancellationType.equals(CancellationType.SELECTIVE);
    }

    boolean isSingleCancellation() {
        return cancellationType.equals(CancellationType.SINGLE);
    }

    public enum CancellationType {
        NONE,
        SELECTIVE,
        SINGLE;
    }
}
