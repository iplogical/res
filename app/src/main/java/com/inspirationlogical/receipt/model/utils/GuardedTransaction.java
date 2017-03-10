package com.inspirationlogical.receipt.model.utils;

import javax.persistence.EntityManager;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class GuardedTransaction {
    @FunctionalInterface
    public interface Functor{
        void doIt();
    }

    public static void Run(EntityManager manager,Functor f) {
        manager.getTransaction().begin();
        try {
            f.doIt();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw e;
        }
    }
}
