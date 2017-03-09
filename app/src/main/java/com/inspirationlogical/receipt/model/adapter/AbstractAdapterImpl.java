package com.inspirationlogical.receipt.model.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.AbstractEntity;

public abstract class AbstractAdapterImpl<T extends AbstractEntity>
    implements AbstractAdapter<T> {

    protected T adaptee;

    protected EntityManager manager;

    public AbstractAdapterImpl(T adaptee, EntityManager manager) {
        this.adaptee = adaptee;
        this.manager = manager;
    }

    @Override
    public T getAdaptee() {
        return adaptee;
    }

}
