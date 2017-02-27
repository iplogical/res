package com.inspirationlogical.receipt.dummy;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class EntityManagerFactoryRule implements TestRule {

    private EntityManagerFactory emf;
    private EntityManager em;

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Properties props = new Properties();
                props.setProperty("persistenceXmlLocation",
                        "/C:/workspace/git/Receipt/app/src/test/resources/META-INF/persistence.xml");
                emf = Persistence.createEntityManagerFactory("TestPersistance");
                em = emf.createEntityManager();
                try {
                    base.evaluate();
                } finally {
                    em.close();
                    emf.close();
                }
            }
        };
    }

    public EntityManager getEntityManager() {
        return em;
    }

}
