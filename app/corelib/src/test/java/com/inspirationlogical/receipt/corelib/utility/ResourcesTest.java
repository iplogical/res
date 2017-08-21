package com.inspirationlogical.receipt.corelib.utility;

import static org.junit.Assert.assertEquals;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;
import org.junit.Test;

public class ResourcesTest {

    @Test
    public void testResources() {
        assertEquals("ResourceString value", Resources.PRINTER.getString("TestResource.First"));
    }
}
