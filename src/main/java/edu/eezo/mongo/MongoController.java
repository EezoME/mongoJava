package edu.eezo.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.MongoSocketException;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.net.ConnectException;
import java.util.*;

/**
 * Created by eezo33 on 03.10.2017.
 */
public class MongoController {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> currentCollection;

    public MongoController(Properties prop) {
        MongoCredential credential = MongoCredential.createCredential(prop.getProperty("login"),
                prop.getProperty("dbname"), prop.getProperty("password").toCharArray());
        mongoClient = new MongoClient(new ServerAddress(prop.getProperty("host"), Integer.parseInt(prop.getProperty("port"))), Arrays.asList(credential));
        database = mongoClient.getDatabase(prop.getProperty("dbname"));
        currentCollection = database.getCollection(prop.getProperty("table"));
    }

    public static Bson getBsonFilterFromMap(Map<String, String> map) {
        Set<String> keys = map.keySet();
        java.util.List<BsonElement> bsonDocumentList = new ArrayList<BsonElement>();

        for (String key : keys) {
            bsonDocumentList.add(new BsonElement(key, new BsonString(map.get(key))));
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

    public void close() {
        database = null;
        mongoClient.close();
    }

    /* COLLECTION API */

    public MongoCollection<Document> createCollection(String collectionName) {
        database.createCollection(collectionName);
        return database.getCollection(collectionName);
    }

    public void setCurrentCollection(MongoCollection<Document> collection) {
        currentCollection = collection;
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

    public void addDocuments(List<? extends Document> documents) {
        currentCollection.insertMany(documents);
    }

    public void addDocumentIntoCollection(MongoCollection<Document> collection, Document document) {
        collection.insertOne(document);
    }

    public void addDocumentsIntoCollection(MongoCollection<Document> collection, List<? extends Document> documents) {
        collection.insertMany(documents);
    }

    public FindIterable<Document> getDocuments(Bson filter) {
        return currentCollection.find(filter);
    }

    public FindIterable<Document> getDocuments(MongoCollection<Document> collection, Bson filter) {
        return collection.find(filter);
    }

    public FindIterable<Document> getAllDocuments(MongoCollection<Document> collection) {
        return collection.find();
    }
}
