package edu.eezo.mongo;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import java.util.*;

/**
 * Created by eezo33 on 03.10.2017.
 */
public class MongoController {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> currentCollection;

    /**
     * Makes a mongo object with defined properties.<br>
     * Properties should be like this:
     * <ul>
     * <li>"host": "..."</li>
     * <li>"port": "..."</li>
     * <li>"dbname": "..."</li>
     * <li>"login": "..."</li>
     * <li>"password": "..."</li>
     * <li>"table": "..."</li>
     * </ul>
     * @param prop defined properties
     */
    public MongoController(Properties prop) {
        try {
            MongoCredential credential = MongoCredential.createCredential(prop.getProperty("login"),
                    prop.getProperty("dbname"), prop.getProperty("password").toCharArray());
            mongoClient = new MongoClient(new ServerAddress(prop.getProperty("host"), Integer.parseInt(prop.getProperty("port"))), Arrays.asList(credential));
            database = mongoClient.getDatabase(prop.getProperty("dbname"));
            checkForNecessaryCollections(new String[]{"users", "books", "authors"});
            currentCollection = database.getCollection(prop.getProperty("table"));
        } catch (MongoSocketException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "MongoDB server is not running.");
        }
    }

    /**
     * Returns a Bson object, constructed with passed filter represented as map.
     * @param map filter
     * @return a BsonDocument object
     */
    public static Bson getBsonFilterFromMap(Map<String, String> map) {
        Set<String> keys = map.keySet();
        List<BsonElement> bsonDocumentList = new ArrayList<>();

        for (String key : keys) {
            bsonDocumentList.add(new BsonElement(key, new BsonString(map.get(key))));
        }

        return new BsonDocument(bsonDocumentList);
    }

    /**
     * Returns a Bson object, constructed with passed filter represented as enumeration of parameters.
     * @param strings filter
     * @return a BsonDocument object
     */
    public static Bson getBsonFilterFormEnumeration(String... strings) {
        List<BsonElement> bsonDocumentList = new ArrayList<>();

        for (int i = 0; i < strings.length; i++) {
            bsonDocumentList.add(new BsonElement(strings[i], new BsonString(strings[i + 1])));
            i++;
        }

        return new BsonDocument(bsonDocumentList);
    }

    static MongoController getDefaultInstance() {
        Properties prop = new Properties();
        prop.setProperty("host", "localhost");
        prop.setProperty("port", "27017");
        prop.setProperty("dbname", "admin");
        prop.setProperty("login", "root");
        prop.setProperty("password", "root");
        prop.setProperty("table", "users");
        return new MongoController(prop);
    }

    public MongoDatabase getCurrentDatabase() {
        return database;
    }

    public void setDatabase(String databaseName) {
        database = mongoClient.getDatabase(databaseName);
    }

    public void setDatabase(MongoDatabase database) {
        this.database = database;
    }

    public void deleteDatabase(String databaseName) {
        mongoClient.dropDatabase(databaseName);

        if (database.getName().equals(databaseName)) {
            database = null;
        }
    }

    /**
     * Resets current database and closes a connection.
     */
    public void close() {
        database = null;
        mongoClient.close();
    }

    /* COLLECTION API */

    public MongoCollection<Document> createCollection(String collectionName) {
        database.createCollection(collectionName);
        return database.getCollection(collectionName);
    }

    public void setCurrentCollection(String collectionName) {
        currentCollection = database.getCollection(collectionName);
    }

    public MongoCollection<Document> getCollection(String collectionName) {
        return database.getCollection(collectionName);
    }

    public void deleteCollection(String collectionName) {
        database.getCollection(collectionName).drop();
    }


    /* DOCUMENT API */

    public void addDocument(Document document) {
        currentCollection.insertOne(document);
    }

    public void addDocument(String collection, Document document) {
        getCollection(collection).insertOne(document);
    }

    public void replaceDocument(String collection, Bson filter, Document document) {
        getCollection(collection).findOneAndReplace(filter, document);
    }

    public void addDocuments(List<? extends Document> documents) {
        currentCollection.insertMany(documents);
    }

    public void addDocuments(String collection, List<? extends Document> documents) {
        getCollection(collection).insertMany(documents);
    }

    public FindIterable<Document> getDocuments(Bson filter) {
        return currentCollection.find(filter);
    }

    public FindIterable<Document> getDocuments(String collection, Bson filter) {
        return getCollection(collection).find(filter);
    }

    public FindIterable<Document> getAllDocuments() {
        return currentCollection.find();
    }

    public FindIterable<Document> getAllDocuments(String collection) {
        return getCollection(collection).find();
    }

    /* Others */

    private void checkForNecessaryCollections(String[] necessaryCollections) {
        for (int i = 0; i < necessaryCollections.length; i++) {
            if (database.getCollection(necessaryCollections[i]) == null) {
                createCollection(necessaryCollections[i]);
                System.out.println("Missed collection '" + necessaryCollections[i] + "' was created.");
            }
        }
    }
}
