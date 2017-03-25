package com.inspirationlogical.receipt.corelib.service;

import lombok.Builder;
import lombok.Data;

/**
 * Created by Bálint on 2017.03.20..
 */

@Builder
public @Data
class AdHocProductParams {

    private String name;

    private int purchasePrice;

    private int salePrice;
}
