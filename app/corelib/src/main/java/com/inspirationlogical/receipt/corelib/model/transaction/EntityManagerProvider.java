package com.inspirationlogical.receipt.corelib.model.transaction;

import com.inspirationlogical.receipt.corelib.utility.resources.Resources;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerProvider {

    private static EntityManagerFactory emf;
    private static EntityManager em;

    public static synchronized EntityManager getEntityManager() {
        if(emf == null) {
            Map<String, String> properties = buildReceiptActualPropoerties();
            emf = Persistence.createEntityManagerFactory("Production", properties);
            em = emf.createEntityManager();
        }
        return em;
    }

    private static Map<String, String> buildReceiptActualPropoerties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("javax.persistence.jdbc.url", Resources.CONFIG.getString("ReceiptActualJdbcUrl"));
        return properties;
    }

    public static EntityManager getTestEntityManager() {
        if(emf == null) {
            emf = Persistence.createEntityManagerFactory("TestPersistence");
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
