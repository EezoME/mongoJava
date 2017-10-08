package edu.eezo.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by eezo33 on 03.10.2017.
 */
public class User extends AbstractEntity {
    private String name;
    private String login;
    private String password;
    private String role;

    private List<String> favoriteBooks;
    private List<String> readBooks;

    public User(String name, String login, String password, String role) {
        this(name, login, password, role, null, null);
    }

    User(String name, String login, String password, String role, List<String> favoriteBooks, List<String> readBooks) {
        this.name = name;
        this.login = login;
        this.password = password;
        this.role = role;

        if (favoriteBooks == null) {
            this.favoriteBooks = new ArrayList<>();
        } else {
            this.favoriteBooks = favoriteBooks;
        }

        if (readBooks == null) {
            this.readBooks = new ArrayList<>();
        } else {
            this.readBooks = readBooks;
        }
    }

    public Document generateDocument() {
        return new Document("name", name).append("login", login).append("password", password).
                append("role", role).append("favoriteBooks", favoriteBooks).append("readBooks", readBooks);
    }

    public static User makeInstanceFromDocument(Document document) {
        String userName = "";
        String userLogin = "";
        String userPassword = "";
        String userRole = "";
        List<String> userFavoriteBooks = null;
        List<String> userReadBooks = null;

        if (document.containsKey("name")) {
            userName = document.getString("name");
        }
        if (document.containsKey("login")) {
            userLogin = document.getString("login");
        }
        if (document.containsKey("password")) {
            userPassword = document.getString("password");
        }
        if (document.containsKey("role")) {
            userRole = document.getString("role");
        }
        if (document.containsKey("favoriteBooks")) {
            userFavoriteBooks = (List<String>) document.get("favoriteBooks");
        }
        if (document.containsKey("readBooks")) {
            userReadBooks = (List<String>) document.get("readBooks");
        }

        return new User(userName, userLogin, userPassword, userRole, userFavoriteBooks, userReadBooks);
    }

    public static List<User> makeListFromIterable(FindIterable<Document> users) {
        MongoCursor<Document> iterator = users.iterator();
        List<User> userList = new ArrayList<>();
        while (iterator.hasNext()) {
            userList.add(makeInstanceFromDocument(iterator.next()));
        }
        return userList;
    }

    public static void displayDataOnTable(JTable table, java.util.List<User> userList) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setColumnIdentifiers(getTableColumnIdentifiers());
        model.setRowCount(0);

        for (int i = 0, length = userList.size(); i < length; i++) {
            model.setRowCount(i + 1);
            model.setValueAt(userList.get(i).getName(), i, 0);
            model.setValueAt(userList.get(i).getLogin(), i, 1);
            model.setValueAt(userList.get(i).getPassword(), i, 2);
            model.setValueAt(userList.get(i).getRole(), i, 3);
            model.setValueAt(userList.get(i).getFavoriteBooks(), i, 4);
            model.setValueAt(userList.get(i).getReadBooks(), i, 5);
        }
    }

    public void addToReadBooks(Book book) {
        addToList(readBooks, book);
    }

    public void addToFavoriteBooks(Book book) {
        addToList(favoriteBooks, book);
    }

    private void addToList(List<String> bookList, Book book) {
        if (book == null) {
            return;
        }

        boolean isInList = false;
        for (String aBook : bookList) {
            if (aBook.equals(book.getTitle())) {
                isInList = true;
            }
        }

        if (!isInList) {
            bookList.add(book.getTitle());
        }
    }

    public void removeFromReadBooks(Book book) {
        removeFromList(readBooks, book);
    }

    public void removeFromFavoriteBooks(Book book) {
        removeFromList(favoriteBooks, book);
    }

    private void removeFromList(List<String> bookList, Book book) {
        if (book == null) {
            return;
        }

        for (int i = 0; i < bookList.size(); i++) {
            if (bookList.get(i).equals(book.getTitle())) {
                bookList.remove(i);
                break;
            }
        }
    }

    public boolean isInReadBookList(Book book) {
        return isBookInList(readBooks, book);
    }

    public boolean isInFavoriteBookList(Book book) {
        return isBookInList(favoriteBooks, book);
    }

    private boolean isBookInList(List<String> bookList, Book book) {
        if (book == null) {
            return false;
        }

        for (String aBook : bookList) {
            if (aBook.equals(book.getTitle())) {
                return true;
            }
        }

        return false;
    }

    public boolean isAdmin() {
        return role.contains("admin");
    }

    public static String[] getTableColumnIdentifiers() {
        return new String[]{"Name", "Login (!)", "Password", "Role", "Fav. Books", "Books"};
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<String> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    public List<String> getReadBooks() {
        return readBooks;
    }

    public void setReadBooks(List<String> readBooks) {
        this.readBooks = readBooks;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return login;
    }
}
