package com.inspirationlogical.receipt.corelib.model.adapter;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static EntityManager getEntityManager() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("ProductionPersistance");
            em = emf.createEntityManager();
        }
        return em;
    }

    public static void closeEntityManager() {
        em.close();
        emf.close();

    }
}
