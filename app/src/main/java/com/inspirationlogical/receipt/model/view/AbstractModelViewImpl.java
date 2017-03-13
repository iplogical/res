package com.inspirationlogical.receipt.model.view;

import com.inspirationlogical.receipt.model.adapter.AbstractAdapter;

/**
 * Created by Bálint on 2017.03.13..
 */
public abstract class AbstractModelViewImpl<T extends AbstractAdapter> {

    protected T adapter;

    public AbstractModelViewImpl(T adapter) {
        this.adapter = adapter;
    }

    public T getAdapter() {
        return adapter;
    }

}
