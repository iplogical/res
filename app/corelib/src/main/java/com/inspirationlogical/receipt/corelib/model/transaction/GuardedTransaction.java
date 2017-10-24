package com.inspirationlogical.receipt.corelib.model.transaction;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

public class GuardedTransaction extends AbstractGuardedTransaction {

    private static final Logger logger = LoggerFactory.getLogger(GuardedTransaction.class);

    private static EntityManager manager = EntityManagerProvider.getEntityManager();

    public static synchronized void run(Functor f, Functor before, Functor after) {
        run(manager, f, before, after);
    }

    public static synchronized void run(Functor f) {
        run(f,()->{}, ()->{});
    }

    public static synchronized <T extends AbstractEntity> List<T> runNamedQuery(String name){
        logger.info("Running named query: " + name);
        Wrapper<List<T>> results = new Wrapper<>();
        run(()->{results.setContent(manager.createNamedQuery(name).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static synchronized <T extends AbstractEntity> List<T> runNamedQuery(String name, NamedQueryCallback namedQueryCallback){
        logger.info("Running named query: " + name + ", with NamedQueryCallback: " + namedQueryCallback);
        Wrapper<List<T>> results = new Wrapper<>();
        run(()->{results.setContent(namedQueryCallback.setup(manager.createNamedQuery(name)).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static synchronized List<Object[]> runNamedQueryWithJoin(String name, NamedQueryCallback namedQueryCallback){
        logger.info("Running named query with join: " + name + ", with NamedQueryCallback: " + namedQueryCallback);
        Wrapper<List<Object[]>> results = new Wrapper<>();
        run(()->{results.setContent(namedQueryCallback.setup(manager.createNamedQuery(name)).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static synchronized <T extends AbstractEntity> List<T> runNamedQuery(String name, String entityGraph, NamedQueryCallback namedQueryCallback) {
        logger.info("Running named query: " + name + ", entityGraph: " + entityGraph + ", with NamedQueryCallback: " + namedQueryCallback);
        Wrapper<List<T>> results = new Wrapper<>();
        EntityGraph graph = manager.createEntityGraph(entityGraph);
        Query query = namedQueryCallback.setup(manager.createNamedQuery(name));
        query.setHint("javax.persistence.loadgraph", graph);
        run(() -> results.setContent(query.getResultList()));
        return results.getContent();
    }

    public static synchronized void persist(AbstractEntity e) {
        logger.info("Persist an entity: " + e);
        run(() -> manager.persist(e), () -> {}, () -> {});
    }

    public static synchronized void detach(AbstractEntity e) {
        logger.info("Detach an entity: " + e);
        manager.detach(e);
    }

    public static synchronized void delete(AbstractEntity e, Functor f) {
        logger.info("Delete an entity: " + e);
        delete(manager, e, f);
    }
}
