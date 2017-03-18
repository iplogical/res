package com.inspirationlogical.receipt.corelib.model.adapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static synchronized EntityManager getEntityManager() {
        return getEntityManager("ProductionPersistance");
    }

    public static synchronized EntityManager getEntityManager(String persitenceUnit) {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory(persitenceUnit);
            em = emf.createEntityManager();
        }
        return em;
    }
    public static void closeEntityManager() {
        em.close();
        emf.close();

    }
}
