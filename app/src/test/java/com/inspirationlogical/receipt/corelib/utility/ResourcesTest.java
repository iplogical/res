package com.inspirationlogical.receipt.corelib.utility;

import static org.junit.Assert.*;

import org.junit.Test;

public class ResourcesTest {

    @Test
    public void testResources() {
        assertEquals("ResourceString value", Resources.PRINTER.getString("TestResource.First"));
    }
}
