package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;

public abstract class AbstractAdapter<T extends AbstractEntity>
{

    protected T adaptee;

    public AbstractAdapter(T adaptee) {
        this.adaptee = adaptee;
    }

    public T getAdaptee() {
        return adaptee;
    }

}
