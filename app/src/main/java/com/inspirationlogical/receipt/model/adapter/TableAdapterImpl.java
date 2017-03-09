package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.model.Receipt;
import com.inspirationlogical.receipt.model.Table;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;

public class TableAdapterImpl extends AbstractAdapterImpl<Table> 
    implements TableAdapter {

    public TableAdapterImpl(Table adaptee, EntityManager manager) {
        super(adaptee, manager);
    }

    @Override
    public ReceiptAdapter getActiveReceipt() {
        Query query = manager.createNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER);
        query.setParameter("number", adaptee.getNumber());
        query.setParameter("status", ReceiptStatus.OPEN);
        Receipt active = (Receipt)query.getSingleResult();
        if(active == null) {
            return null;
        }
        else return new ReceiptAdapterImpl(active, manager);
    }

}
