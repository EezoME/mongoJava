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
public class Book extends AbstractEntity {
    private String title;
    private Author author;
    private int year;
    private String genre;
    private double rating;

    public Book(String title, Author author, int year, String genre, double rating) {
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
    }

    public Document generateDocument() {
        return new Document("title", title).append("author", author.toString()).append("year", year).
                append("genre", genre).append("rating", rating);
    }

    public static Book makeInstanceFromDocument(Document document) {
        String bookTitle = "";
        Author bookAuthor = null;
        int bookYear = 0;
        String bookGenre = "";
        double bookRating = 0.0;

        if (document.containsKey("title")) {
            bookTitle = document.getString("title");
        }
        if (document.containsKey("author")) {
            bookAuthor = (Author) document.get("author");
        }
        if (document.containsKey("year")) {
            bookYear = document.getInteger("year");
        }
        if (document.containsKey("genre")) {
            bookGenre = document.getString("genre");
        }
        if (document.containsKey("rating")) {
            bookRating = document.getDouble("rating");
        }

        return new Book(bookTitle, bookAuthor, bookYear, bookGenre, bookRating);
    }

    public static List<Book> makeListFromIterable(FindIterable<Document> books) {
        MongoCursor<Document> iterator = books.iterator();
        List<Book> bookList = new ArrayList<>();
        while (iterator.hasNext()) {
            bookList.add(makeInstanceFromDocument(iterator.next()));
        }
        return bookList;
    }

    public static String[] getTableColumnIdentifiers(){
        return new String[]{"Title", "Author", "Year", "Genre", "Rating"};
    }


    public static void displayDataOnTable(JTable table, java.util.List<Book> bookList) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(getTableColumnIdentifiers());
        model.setRowCount(0);

        for (int i = 0, length = bookList.size(); i < length; i++) {
            model.setRowCount(i+1);
            model.setValueAt(bookList.get(i).getTitle(), i, 0);
            model.setValueAt(bookList.get(i).getAuthor(), i, 1);
            model.setValueAt(bookList.get(i).getYear(), i, 2);
            model.setValueAt(bookList.get(i).getGenre(), i, 3);
            model.setValueAt(bookList.get(i).getRating(), i, 4);
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return title;
    }
}
