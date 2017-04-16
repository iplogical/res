package com.inspirationlogical.receipt.corelib.model.utils;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

/**
 * Created by Ferenc on 2017. 03. 10..
 */
public class GuardedTransaction {

    private static EntityManager manager = EntityManagerProvider.getEntityManager();

    /**
     * A Guarded transaction will execute the functor inside a transaction
     * If the current manager's transaction is already in progress it will just execute the functor
     * without establishing a new transaction
     *
     * @param f a functor which will be executed inside the transaction
     * @exception Exception in case of an exception the transaction will be rolled back, but the persistence objects
     * are going to get in DETACHED state!
     */
    public static void run(Functor f, Functor before, Functor after) {
        boolean myTransaction = !manager.getTransaction().isActive();
        if(myTransaction){
            manager.getTransaction().begin();
        }
        try {
            before.doIt();
            f.doIt();
            after.doIt();
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

    public static <T extends AbstractEntity> List<T> runNamedQuery(String name){
        Wrapper<List<T>> results = new Wrapper<>();
        run(()->{results.setContent(manager.createNamedQuery(name).getResultList());},()->{},()->{});
        return results.getContent();
    }

    @FunctionalInterface
    public interface NamedQueryCallback {
        Query setup(Query query);
    }

    public static <T extends AbstractEntity> List<T> runNamedQuery(String name, NamedQueryCallback namedQueryCallback){
        Wrapper<List<T>> results = new Wrapper<>();
        run(()->{results.setContent(namedQueryCallback.setup(manager.createNamedQuery(name)).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static void runWithRefresh(AbstractEntity e, Functor f) {
        run(f,()->manager.refresh(e), () -> {});
    }

    public static void persist(AbstractEntity e) {
        run(() -> manager.persist(e), () -> {}, () -> {});
    }

    public static void delete(AbstractEntity e, Functor f) {
        run(f,()->{}, ()->{
            manager.remove(e);
        });
    }

    public static void run(Functor f) {
        run(f,()->{}, ()->{});
    }
}
