package com.inspirationlogical.receipt.corelib.model.utils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    private static EntityManagerFactory emfArchive;
    private static EntityManager emArchive;

    public static synchronized EntityManager getEntityManager() {
        return getEntityManager("ProductionPersistance");
    }

    public static synchronized EntityManager getEntityManagerArchive() {
        if(emfArchive == null) {
            emfArchive = Persistence.createEntityManagerFactory("ProductionArchive");
            emArchive = emfArchive.createEntityManager();
        }
        return emArchive;
    }

    public static synchronized EntityManager getEntityManager(String persistenceUnit) {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory(persistenceUnit);
            em = emf.createEntityManager();
        }
        return em;
    }
    public static synchronized void closeEntityManager() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }
}
