package com.inspirationlogical.receipt.model.adapter;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static EntityManager getEntityManager() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("TestPersistance");
            em = emf.createEntityManager();
        }
        return em;
    }

    public static void closeEntityManager() {
        em.close();
        emf.close();

    }
}
