package com.inspirationlogical.receipt.model.adapter;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.model.entity.Receipt;
import com.inspirationlogical.receipt.model.entity.Table;
import com.inspirationlogical.receipt.model.enums.ReceiptStatus;

public class TableAdapter extends AbstractAdapter<Table>
{

    public TableAdapter(Table adaptee, EntityManager manager) {
        super(adaptee, manager);
    }


    public ReceiptAdapter getActiveReceipt() {
        Query query = manager.createNamedQuery(Receipt.GET_RECEIPT_BY_STATUS_AND_OWNER);
        query.setParameter("number", adaptee.getNumber());
        query.setParameter("status", ReceiptStatus.OPEN);
        Receipt active = (Receipt)query.getSingleResult();
        if(active == null) {
            return null;
        }
        else return new ReceiptAdapter(active, manager);
    }

}
