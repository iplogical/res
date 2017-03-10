package com.inspirationlogical.receipt.model.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.AbstractEntity;

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
