package com.inspirationlogical.receipt.corelib.service;

import javax.persistence.EntityManager;

public abstract class AbstractServices {

    protected EntityManager manager;

    protected AbstractServices(EntityManager manager) {
        this.manager = manager;
    }
}
