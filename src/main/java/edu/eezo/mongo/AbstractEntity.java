package edu.eezo.mongo;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eezo33 on 05.10.2017.
 */
public abstract class AbstractEntity extends Document {
    public abstract Document generateDocument();


    /* List of static methods that must be overridden */

    public static AbstractEntity makeInstanceFromDocument(Document document) {
        return new AbstractEntity() {
            @Override
            public Document generateDocument() {
                return null;
            }
        };
    }

    public static List<? extends AbstractEntity> makeListFromIterable(FindIterable<Document> entities) {
        return new ArrayList<>();
    }

    public static String[] getTableColumnIdentifiers() {
        return null;
    }

    /* Also
    public static void displayDataOnTable(JTable table, java.util.List<? extends AbstractEntity> entitiesList) {}
    I know it's bad :( maybe I'll find better way */

    /* End of list */
}
