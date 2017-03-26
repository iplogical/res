package com.inspirationlogical.receipt.corelib.model.adapter;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractAdapter<T extends AbstractEntity>
{

    @Getter
    @Setter
    protected T adaptee;

    public AbstractAdapter(T adaptee) {
        this.adaptee = adaptee;
    }
}
