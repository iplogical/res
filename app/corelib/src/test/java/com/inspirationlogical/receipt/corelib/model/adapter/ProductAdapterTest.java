package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.ProductCategoryType;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bálint on 2017.03.20..
 */
public class ProductAdapterTest {

    @Rule
    public final BuildTestSchemaRule schema = new BuildTestSchemaRule();

    @Test
    public void testGetAdHocProduct() {
        ProductAdapter adHocProduct = ProductAdapter.getAdHocProduct(schema.getEntityManager());
        assertEquals(ProductType.AD_HOC_PRODUCT, adHocProduct.getAdaptee().getType());
    }

}
