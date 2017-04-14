package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Bálint on 2017.03.20..
 */

@Builder
public @Data class AdHocProductParams {

    private String name;

    private int quantity;

    private int purchasePrice;

    private int salePrice;
}
