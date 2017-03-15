package com.inspirationlogical.receipt.model;

import java.util.Collections;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceProperty;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class EntityManagerFactoryRule implements TestRule {

    private EntityManagerFactory emf = EntityManagerFactoryHolder.get();
    private EntityManagerFactory emfView = EntityManagerFactoryHolder.getView();
    private EntityManager em;
    private TestType testType = TestType.DROP_AND_CREATE;

    public EntityManagerFactoryRule(TestType testType) {
        this.testType = testType;
    }

    public EntityManagerFactoryRule() {}

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                if(testType == TestType.DROP_AND_CREATE) {
                    em = emf.createEntityManager();
                } else if(testType == TestType.CREATE) {
                    Properties props = new Properties();
                    props.setProperty("javax.persistence.schema-generation.database.action", "drop-and-create");
                    emfView = Persistence.createEntityManagerFactory("TestViewPersistence", props);
                    em = emfView.createEntityManager();
                } else if(testType == TestType.VALIDATE){
                    em = emfView.createEntityManager();
                }
                try {
                    base.evaluate();
                } finally {
                    em.close();
                }
            }
        };
    }

    public EntityManager getEntityManager() {
        return em;
    }

}
