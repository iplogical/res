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
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("Production");
            em = emf.createEntityManager();
        }
        return em;
    }

    public static synchronized EntityManager getEntityManagerArchive() {
        if(emfArchive == null) {
            emfArchive = Persistence.createEntityManagerFactory("ProductionArchive");
            emArchive = emfArchive.createEntityManager();
        }
        return emArchive;
    }

    public static EntityManager getTestEntityManager() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("TestPersistence");
            em = emf.createEntityManager();
        }
        return em;
    }

    public static EntityManager getTestEntityManagerArchive() {
        if(emfArchive == null) {
            emfArchive = Persistence.createEntityManagerFactory("TestPersistenceArchive");
            emArchive = emfArchive.createEntityManager();
        }
        return emArchive;
    }

    public static synchronized void closeEntityManager() {
        if (em != null) {
            em.close();
        }
        if (emf != null) {
            emf.close();
        }
    }

    public static synchronized void closeEntityManagerArchive() {
        if (emArchive != null) {
            emArchive.close();
        }
        if (emfArchive != null) {
            emfArchive.close();
        }
    }
}
