package com.inspirationlogical.receipt.corelib.model.adapter;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.BuildTestSchemaRule;
import com.inspirationlogical.receipt.corelib.model.enums.ProductType;

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

    @Test
    public void testGetProductByName() {
        assertEquals(1, ProductAdapter.getProductByName("product").size());
    }
}
