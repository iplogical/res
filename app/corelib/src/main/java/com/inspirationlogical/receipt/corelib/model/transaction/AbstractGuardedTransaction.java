package com.inspirationlogical.receipt.corelib.model.transaction;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public abstract class AbstractGuardedTransaction {
    final static Logger logger = LoggerFactory.getLogger(AbstractGuardedTransaction.class);

    @FunctionalInterface
    public interface NamedQueryCallback {
        Query setup(Query query);
    }

    /**
     * A Guarded transaction will execute the functor inside a transaction
     * If the current manager's transaction is already in progress it will just execute the functor
     * without establishing a new transaction
     *
     * @param f a functor which will be executed inside the transaction
     * @exception Exception in case of an exception the transaction will be rolled back, but the persistence objects
     * are going to get in DETACHED state!
     */
    protected static void run(EntityManager manager, Functor f, Functor before, Functor after) {
        boolean myTransaction = !manager.getTransaction().isActive();
        if(myTransaction){
            logger.info("Beginning a transaction.");
            manager.getTransaction().begin();
        }
        try {
            before.doIt();
            f.doIt();
            after.doIt();
            if(myTransaction) {
                logger.info("Commiting a transaction.");
                manager.getTransaction().commit();
                logger.info("A transaction was successfully committed.");
            }
        } catch (Exception e){
            logger.error("Exception in GuardedTransaction", e);
            if(manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            throw e;
        }
    }

    protected static void delete(EntityManager em, AbstractEntity e, Functor f) {
        GuardedTransaction.run(f,()->{}, ()->{
            em.remove(e);
        });
    }
}
