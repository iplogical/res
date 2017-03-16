package com.inspirationlogical.receipt.corelib.model.view;

import com.inspirationlogical.receipt.corelib.model.adapter.AbstractAdapter;

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
