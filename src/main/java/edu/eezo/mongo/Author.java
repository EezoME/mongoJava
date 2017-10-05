package edu.eezo.mongo;

import org.bson.Document;

import java.util.List;

/**
 * Created by eezo33 on 05.10.2017.
 */
public class Author extends AbstractEntity {
    private String name;
    private List<Book> books;

    public Author(String name, List<Book> books) {
        this.name = name;
        this.books = books;
    }

    public Document generateDocument() {
        return new Document("name", name).append("books", books);
    }

    public static Author makeInstanceFromDocument(Document document) {
        String authorName = "";
        List<Book> authorBooks = null;

        if (document.containsKey("name")) {
            authorName = document.getString("name");
        }
        if (document.containsKey("books")) {
            authorBooks = (List<Book>) document.get("books");
        }

        return new Author(authorName, authorBooks);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    @Override
    public String toString() {
        return name;
    }
}
