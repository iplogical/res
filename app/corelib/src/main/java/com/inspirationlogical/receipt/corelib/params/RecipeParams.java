package com.inspirationlogical.receipt.corelib.params;

import lombok.Builder;
import lombok.Data;

/**
 * Created by TheDagi on 2017. 04. 15..
 */
@Builder
public @Data class RecipeParams {

    private String componentName;

    private double quantity;
}
