package edu.eezo.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by eezo33 on 05.10.2017.
 */
public class Author extends AbstractEntity {
    private String name;
    private List<String> books;

    public Author(String name, List<String> books) {
        this.name = name;
        this.books = books;
    }

    public Document generateDocument() {
        return new Document("name", name).append("books", books);
    }

    public static Author makeInstanceFromDocument(Document document) {
        String authorName = "";
        List<String> authorBooks = null;

        if (document.containsKey("name")) {
            authorName = document.getString("name");
        }
        if (document.containsKey("books")) {
            authorBooks = (List<String>) document.get("books");
        }

        return new Author(authorName, authorBooks);
    }

    public static Author getInstanceFromTableCell(Object authorCell, Object bookCell) {
        Author author;
        try {
            author = (Author) authorCell;
        } catch (ClassCastException e) {
            List<String> books = new ArrayList<>();
            books.add(bookCell.toString());
            author = new Author(authorCell.toString(), books);
        }
        return author;
    }

    public static List<Author> makeListFromIterable(FindIterable<Document> authors) {
        MongoCursor<Document> iterator = authors.iterator();
        List<Author> authorList = new ArrayList<>();
        while (iterator.hasNext()) {
            authorList.add(makeInstanceFromDocument(iterator.next()));
        }
        return authorList;
    }

    public static String[] getTableColumnIdentifiers() {
        return new String[]{"Name (!)", "Books"};
    }

    public static void displayDataOnTable(JTable table, java.util.List<Author> authorList) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(getTableColumnIdentifiers());
        model.setRowCount(0);

        for (int i = 0, length = authorList.size(); i < length; i++) {
            model.setRowCount(i+1);
            model.setValueAt(authorList.get(i).getName(), i, 0);
            model.setValueAt(authorList.get(i).getBooks(), i, 1);
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getBooks() {
        return books;
    }

    public void setBooks(List<String> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return name;
    }
}
