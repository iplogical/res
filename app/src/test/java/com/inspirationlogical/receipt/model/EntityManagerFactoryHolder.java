package com.inspirationlogical.receipt.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
class EntityManagerFactoryHolder{
    static private EntityManagerFactory emf;
    static private EntityManagerFactory emfView;
    static{
        emf = Persistence.createEntityManagerFactory("TestPersistance");
        emfView = Persistence.createEntityManagerFactory("TestViewPersistence");
    }

    static public EntityManagerFactory get(){return emf;}
    static public EntityManagerFactory getView(){return emfView;}
}
