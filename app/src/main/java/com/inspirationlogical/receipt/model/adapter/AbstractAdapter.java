package com.inspirationlogical.receipt.model.adapter;

import java.util.List;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.AbstractEntity;

public interface AbstractAdapter<T extends AbstractEntity> {

    T getAdaptee();
}
