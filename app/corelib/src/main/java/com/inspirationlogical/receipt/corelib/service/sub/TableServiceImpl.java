package com.inspirationlogical.receipt.corelib.service.sub;

import com.inspirationlogical.receipt.corelib.model.entity.Table;
import com.inspirationlogical.receipt.corelib.repository.ReceiptRepository;
import com.inspirationlogical.receipt.corelib.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Scope(value = "prototype")
public class TableServiceImpl implements TableService {

    private Table adaptee;

    public TableServiceImpl(@Lazy Table adaptee) {
        this.adaptee = adaptee;
    }

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Override
    public void setNumber(int tableNumber) {
        System.out.println("Hello Spring!");
    }
}
