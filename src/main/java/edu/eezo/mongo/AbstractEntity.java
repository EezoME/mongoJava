package edu.eezo.mongo;

import org.bson.Document;

/**
 * Created by eezo33 on 05.10.2017.
 */
public abstract class AbstractEntity extends Document {
    public abstract Document generateDocument();
    public static AbstractEntity makeInstanceFromDocument(Document document) {
        return new AbstractEntity() {
            @Override
            public Document generateDocument() {
                return null;
            }
        };
    }
}
