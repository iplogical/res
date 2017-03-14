package com.inspirationlogical.receipt.model;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class EntityManagerFactoryRule implements TestRule {

    static private EntityManagerFactory emf;
    private EntityManager em;
    private TestType testType = TestType.DROP_AND_CREATE;

    static{
        Properties props = new Properties();
        TestType  testType = TestType.DROP_AND_CREATE;
        if(testType == TestType.CREATE) {
            props.setProperty("persistenceXmlLocation", "resources/META-INF/persistence.xml");
            props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/ReceiptViewTest");
            props.setProperty("javax.persistence.schema-generation.database.action", "drop-and-create");
            emf = Persistence.createEntityManagerFactory("TestPersistance", props);
        } else if (testType == TestType.VALIDATE){
            props.setProperty("persistenceXmlLocation", "resources/META-INF/persistence.xml");
            props.setProperty("javax.persistence.jdbc.url", "jdbc:mysql://localhost:3306/ReceiptViewTest");
            props.setProperty("javax.persistence.schema-generation.database.action", "validate");
            emf = Persistence.createEntityManagerFactory("TestPersistance", props);
        } else if(testType == TestType.DROP_AND_CREATE){
            emf = Persistence.createEntityManagerFactory("TestPersistance");
        }
        emf = Persistence.createEntityManagerFactory("TestPersistance");
    }

    public EntityManagerFactoryRule(TestType testType) {
        this.testType = testType;
    }

    public EntityManagerFactoryRule() {}

    @Override
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                em = emf.createEntityManager();
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
