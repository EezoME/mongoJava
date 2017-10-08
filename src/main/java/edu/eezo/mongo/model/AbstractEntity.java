package edu.eezo.mongo.model;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eezo33 on 05.10.2017.
 */
public abstract class AbstractEntity extends Document {
    /**
     * Makes a list of string from table cell object.
     *
     * @param cell table cell
     * @return a list of strings
     */
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

    /**
     * Generates a Document object according to current classes fields.
     *
     * @return a Document object
     */
    public abstract Document generateDocument();

    /* List of static methods that must be overridden */
    /*
    public static <? extends AbstractEntity> makeInstanceFromDocument(Document document) {}
    public static List<? extends AbstractEntity> makeListFromIterable(FindIterable<Document> entities) {}
    public static String[] getTableColumnIdentifiers() {}
    public static void displayDataOnTable(JTable table, java.util.List<? extends AbstractEntity> entitiesList) {}

    I know it's bad :( maybe I'll find better way.
    */
}
