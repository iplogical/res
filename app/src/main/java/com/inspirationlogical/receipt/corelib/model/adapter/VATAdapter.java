package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.VAT;
import com.inspirationlogical.receipt.corelib.model.enums.ReceiptRecordType;
import com.inspirationlogical.receipt.corelib.model.enums.VATStatus;

/**
 * Created by Bálint on 2017.03.17..
 */
public class VATAdapter extends AbstractAdapter<VAT> {

    public static VATAdapter getVatByName(ReceiptRecordType type, VATStatus status) {
        return new VATAdapter((VAT)EntityManagerProvider.getEntityManager().createNamedQuery(VAT.GET_VAT_BY_NAME)
                .setParameter("name", ReceiptRecordType.getVatName(type))
                .setParameter("status", status)
                .getResultList().get(0));
    }

    public VATAdapter(VAT adaptee) {
        super(adaptee);
    }
}
