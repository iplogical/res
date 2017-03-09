package com.inspirationlogical.receipt.model.adapter;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {

    private static EntityManagerFactory emf;
    private static List<EntityManager> emList;

    public static EntityManager getEntityManager() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("TestPersistance");
        }
        EntityManager em = emf.createEntityManager();
        emList.add(em);
        return em;
    }

    public static void closeEntityManager(EntityManager em) {
        emList.remove(em);
        em.close();
        if(emList.isEmpty()) {
            emf.close();
        }
    }
}
