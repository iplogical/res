package com.inspirationlogical.receipt.corelib.model.utils;

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
     * A Guarded transaction will execute the functor inside a transaction
     * If the current manager's transaction is already in progress it will just execute the functor
     * without establishing a new transaction
     *
     * @param manager the entity manager used for the transaction
     * @param f a functor which will be executed inside the transaction
     * @exception Exception in case of an exception the transaction will be rolled back, but the persistence objects
     * are going to get in DETACHED state!
     */
    public static void Run(EntityManager manager,Functor f) {
        boolean myTransaction = !manager.getTransaction().isActive();
        if(myTransaction){
            manager.getTransaction().begin();
        }
        try {
            f.doIt();
            if(myTransaction) {
                manager.getTransaction().commit();
            }
        } catch (Exception e){
            if(manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            throw e;
        }
    }
}