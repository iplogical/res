package com.inspirationlogical.receipt.corelib.model.transaction;

import com.inspirationlogical.receipt.corelib.model.entity.AbstractEntity;
import com.inspirationlogical.receipt.corelib.utility.Wrapper;

import javax.persistence.EntityManager;
import java.util.List;

public class GuardedTransactionArchive extends AbstractGuardedTransaction {

    private static EntityManager manager = EntityManagerProvider.getEntityManagerArchive();

    public static synchronized void run(Functor f, Functor before, Functor after) {
        run(manager, f, before, after);
    }

    public static synchronized void run(Functor f) {
        run(f, () -> {}, () -> {});
    }

    public static synchronized <T extends AbstractEntity> List<T> runNamedQuery(String name){
        Wrapper<List<T>> results = new Wrapper<>();
        run(()->{results.setContent(manager.createNamedQuery(name).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static synchronized <T extends AbstractEntity> List<T> runNamedQuery(String name, NamedQueryCallback namedQueryCallback){
        Wrapper<List<T>> results = new Wrapper<>();
        run(()->{results.setContent(namedQueryCallback.setup(manager.createNamedQuery(name)).getResultList());},()->{},()->{});
        return results.getContent();
    }

    public static synchronized void persist(AbstractEntity e) {
        run(() -> manager.persist(e), () -> {}, () -> {});
    }

    public static synchronized void delete(AbstractEntity e, Functor f) {
        delete(manager, e, f);
    }
}
