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

    /**
     *
     * @param manager the entity manager used for the transaction
     * @param f a functor which will be executed inside the transaction
     * @exception Exception in case of an exception the transaction will be rolled back, but the persistence objects
     * are going to get in DETACHED state!
     */
    public static void Run(EntityManager manager,Functor f) {
        manager.getTransaction().begin();
        try {
            f.doIt();
            manager.getTransaction().commit();
        } catch (Exception e){
            if(manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            throw e;
        }
    }
}
