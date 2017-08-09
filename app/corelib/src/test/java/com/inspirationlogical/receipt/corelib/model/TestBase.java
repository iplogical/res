package com.inspirationlogical.receipt.corelib.model;

import com.inspirationlogical.receipt.corelib.model.utils.BuildTestSchema;
import org.junit.Before;

public class TestBase {

    protected BuildTestSchema schema;

    @Before
    public void buildTestSchema() {
        schema = new BuildTestSchema();
        schema.buildTestSchema();
    }
}
