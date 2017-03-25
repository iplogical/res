package com.inspirationlogical.receipt.corelib.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
public class EntityManagerFactoryHolder{
    static private EntityManagerFactory emf;
    static private EntityManagerFactory emfView;
    static{
        emf = Persistence.createEntityManagerFactory("TestPersistance");
        emfView = Persistence.createEntityManagerFactory("TestViewPersistence");
    }

    static public EntityManagerFactory get(){return emf;}
    static public EntityManagerFactory getView(){return emfView;}
}
