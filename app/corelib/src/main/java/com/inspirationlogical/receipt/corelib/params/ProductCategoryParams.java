package com.inspirationlogical.receipt.corelib.params;

import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductStatus;
import com.inspirationlogical.receipt.corelib.model.view.ProductCategoryView;
import lombok.Builder;
import lombok.Data;

/**
 * Created by régiDAGi on 2017. 04. 12..
 */
@Builder
public @Data
class ProductCategoryParams {
    private ProductCategoryView parent;

    private String name;

    private String originalName;

    private ProductCategoryType type;

    private ProductStatus status;

    private int orderNumber;
}
