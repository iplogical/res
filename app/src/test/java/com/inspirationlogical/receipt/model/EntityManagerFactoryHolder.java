package com.inspirationlogical.receipt.model;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Properties;

/**
 * Created by Ferenc on 2017. 03. 14..
 */
class EntityManagerFactoryHolder{
    static private EntityManagerFactory emf;
    static{
        emf = Persistence.createEntityManagerFactory("TestPersistance");
    }

    static public EntityManagerFactory get(){return emf;}
}
