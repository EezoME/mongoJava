package edu.eezo.mongo;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Properties;

/**
 * Created by eezo33 on 04.10.2017.
 */
public class MongoControllerTest {
    MongoController mongo;

    @BeforeMethod
    public void setUp() throws Exception {
        Properties prop = new Properties();
        prop.setProperty("host", "localhost");
        prop.setProperty("port", "27017");
        prop.setProperty("dbname", "admin");
        prop.setProperty("login", "root");
        prop.setProperty("password", "root");
        prop.setProperty("table", "users");

        mongo = new MongoController(prop);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        mongo.close();
    }
/*
//    @Test
//    public void testConnection() throws Exception {
//        boolean authenticate = mongo.isAuthenticate();
//        Assert.assertEquals(authenticate, true);
//    }

    @Test
    public void testAddUser() throws Exception {
        mongo.addDocument(new User("test"));
    }

    @Test
    public void testGet() throws Exception {
        User user = mongo.getByLogin("test");
        System.out.println(user);
        Assert.assertNotEquals(user, null);
    }

    @Test
    public void testDelete() throws Exception {
        Assert.assertEquals(mongo.deleteByLogin("test"), true);
    }

    @Test
    public void testUpdate() throws Exception {
        Assert.assertEquals(mongo.updateByLogin("test", "DevColibri"), true);
    }*/
}
