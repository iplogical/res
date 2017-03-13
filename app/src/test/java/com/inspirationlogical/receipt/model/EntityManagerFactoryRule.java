package com.inspirationlogical.receipt.model;

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
                Properties props = new Properties();
                if(testType == TestType.CREATE) {
                    props.setProperty("persistenceXmlLocation", "resources/META-INF/persistence.xml");
                    props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/ReceiptViewTest");
                    props.setProperty("javax.persistence.schema-generation.database.action", "create");
                    emf = Persistence.createEntityManagerFactory("TestPersistance", props);
                } else if (testType == TestType.VALIDATE){
                    props.setProperty("persistenceXmlLocation", "resources/META-INF/persistence.xml");
                    props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/ReceiptViewTest");
                    props.setProperty("javax.persistence.schema-generation.database.action", "validate");
                    emf = Persistence.createEntityManagerFactory("TestPersistance", props);
                } else if(testType == TestType.DROP_AND_CREATE){
                        emf = Persistence.createEntityManagerFactory("TestPersistance");
                }
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
