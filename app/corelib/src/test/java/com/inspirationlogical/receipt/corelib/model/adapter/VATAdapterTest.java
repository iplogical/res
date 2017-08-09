package com.inspirationlogical.receipt.corelib.model.adapter;

import static junit.framework.TestCase.assertEquals;

import com.inspirationlogical.receipt.corelib.model.TestBase;
import org.junit.Test;

import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATName;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;

/**
 * Created by Bálint on 2017.03.17..
 */
public class VATAdapterTest extends TestBase {

    @Test
    public void testGetVatByReceiptRecordTypeHere() {
        VATAdapter vatAdapter = VATAdapter.getVatByName(ReceiptRecordType.HERE, VATStatus.VALID);
        assertEquals(VATName.REDUCED, vatAdapter.getAdaptee().getName());
    }

    @Test
    public void testGetVatByReceiptRecordTypeTakeAway() {
        VATAdapter vatAdapter = VATAdapter.getVatByName(ReceiptRecordType.TAKE_AWAY, VATStatus.VALID);
        assertEquals(VATName.NORMAL, vatAdapter.getAdaptee().getName());
    }
}
