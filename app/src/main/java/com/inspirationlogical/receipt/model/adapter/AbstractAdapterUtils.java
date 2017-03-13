package com.inspirationlogical.receipt.model.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import com.inspirationlogical.receipt.model.entity.AbstractEntity;

public class AbstractAdapterUtils {

    public static <AdapterT extends AbstractAdapter<AdapteeT>, AdapteeT extends AbstractEntity>
    List<AdapterT> createFromAdaptees(List<AdapteeT> adaptees, EntityManager manager) {
        final List<AdapterT> adapters = new ArrayList<AdapterT>();
        adaptees.forEach((adaptee) -> {
            //adapters.add();
        });
        return adapters;
    }

}
