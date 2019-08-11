package com.inspirationlogical.receipt.corelib.service.vat;

import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.entity.VATSerie;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;

public interface VATService {

    VATSerie findValidVATSerie();
}
