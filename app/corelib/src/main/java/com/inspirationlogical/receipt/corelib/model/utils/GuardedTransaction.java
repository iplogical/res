package com.inspirationlogical.receipt.corelib.model.utils;

import com.inspirationlogical.receipt.corelib.model.adapter.EntityManagerProvider;
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
public class GuardedTransaction {
    final static Logger logger = LoggerFactory.getLogger(GuardedTransaction.class);

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
            logger.error("Exception in GuardedTransaction", e);
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

    public static List<Object[]> runNamedQueryWithJoin(String name, NamedQueryCallback namedQueryCallback){
        Wrapper<List<Object[]>> results = new Wrapper<>();
        run(()->{results.setContent(namedQueryCallback.setup(manager.createNamedQuery(name)).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static <T extends AbstractEntity> List<T> runNamedQuery(String name, String entityGraph, NamedQueryCallback namedQueryCallback) {
        Wrapper<List<T>> results = new Wrapper<>();
        EntityGraph graph = manager.createEntityGraph(entityGraph);
        Query query = namedQueryCallback.setup(manager.createNamedQuery(name));
        query.setHint("javax.persistence.loadgraph", graph);
        run(() -> results.setContent(query.getResultList()));
        return results.getContent();
    }

    public static void runWithRefresh(AbstractEntity e, Functor f) {
        run(f,()->manager.refresh(e), () -> {});
    }

    public static void persist(AbstractEntity e) {
        run(() -> manager.persist(e), () -> {}, () -> {});
    }

    public static void merge(AbstractEntity e) {
        run(() -> manager.merge(e), () -> {}, () -> {});
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
