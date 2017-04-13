package com.inspirationlogical.receipt.corelib.service;

import com.inspirationlogical.receipt.corelib.model.entity.PriceModifier;
import lombok.Builder;
import lombok.Data;

/**
 * Created by TheDagi on 2017. 04. 13..
 */
@Builder
public @Data class PriceModifierParams {

    private String originalName;

    private String ownerName;

    private boolean isCategory;

    private PriceModifier.PriceModifierBuilder builder;
}
