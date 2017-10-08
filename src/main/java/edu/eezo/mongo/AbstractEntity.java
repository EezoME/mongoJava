package edu.eezo.mongo;

import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eezo33 on 05.10.2017.
 */
public abstract class AbstractEntity extends Document {
    public abstract Document generateDocument();

    public static List<String> parseTableCell(Object cell) {
        List<String> strings;
        try {
            strings = (List<String>) cell;
        } catch (ClassCastException e) {
            String cellAsString = cell.toString();

            if (cellAsString.isEmpty()) {
                return new ArrayList<>();
            }

            int startIndex = cellAsString.indexOf('['),
                    endIndex = cellAsString.lastIndexOf(']');
            startIndex = startIndex >= 0 ? startIndex + 1 : 0;
            endIndex = endIndex != -1 ? endIndex : cellAsString.length() - 1;

            if (startIndex == endIndex) {
                return new ArrayList<>();
            }

            cellAsString = cellAsString.substring(startIndex, endIndex);
            strings = Arrays.asList(cellAsString.split(","));
            for (int i = 0; i < strings.size(); i++) {
                strings.set(i, strings.get(i).trim());
            }
        }
        return strings;
    }

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
