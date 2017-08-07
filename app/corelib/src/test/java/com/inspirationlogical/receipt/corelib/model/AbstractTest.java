package com.inspirationlogical.receipt.corelib.model;

import org.junit.Before;

public class AbstractTest {

    protected BuildTestSchema schema;

    @Before
    public void buildTestSchema() {
        schema = new BuildTestSchema();
        schema.buildTestSchema();
    }
}
