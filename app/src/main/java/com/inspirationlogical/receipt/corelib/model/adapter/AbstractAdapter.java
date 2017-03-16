package com.inspirationlogical.receipt.corelib.model.adapter;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;

public abstract class AbstractAdapter<T extends AbstractEntity>
{

    protected T adaptee;

    protected EntityManager manager;

    public AbstractAdapter(T adaptee, EntityManager manager) {
        this.adaptee = adaptee;
        this.manager = manager;
    }

    public T getAdaptee() {
        return adaptee;
    }

}
