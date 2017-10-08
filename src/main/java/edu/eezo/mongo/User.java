package edu.eezo.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eezo33 on 03.10.2017.
 */
public class User extends AbstractEntity {
    private String name;
    private String login;
    private String password;
    private boolean isAdmin;

    private List<Book> favoriteBooks;
    private List<Book> readBooks;

    public User(String name, String login, String password) {
        this(name, login, password, null, null);
    }

    private User(String name, String login, String password, List<Book> favoriteBooks, List<Book> readBooks) {
        this.name = name;
        this.login = login;
        this.password = password;

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
                append("favoriteBooks", favoriteBooks).append("readBooks", readBooks);
    }

    public static User makeInstanceFromDocument(Document document) {
        String userName = "";
        String userLogin = "";
        String userPassword = "";
        List<Book> userFavoriteBooks = null;
        List<Book> userReadBooks = null;

        if (document.containsKey("name")) {
            userName = document.getString("name");
        }
        if (document.containsKey("login")) {
            userLogin = document.getString("login");
        }
        if (document.containsKey("password")) {
            userPassword = document.getString("password");
        }
        if (document.containsKey("favoriteBooks")) {
            userFavoriteBooks = (List<Book>) document.get("favoriteBooks");
        }
        if (document.containsKey("readBooks")) {
            userReadBooks = (List<Book>) document.get("readBooks");
        }

        return new User(userName, userLogin, userPassword, userFavoriteBooks, userReadBooks);
    }

    public static List<User> makeListFromIterable(FindIterable<Document> users) {
        MongoCursor<Document> iterator = users.iterator();
        List<User> userList = new ArrayList<>();
        while (iterator.hasNext()) {
            userList.add(makeInstanceFromDocument(iterator.next()));
        }
        return userList;
    }

    public static String[] getTableColumnIdentifiers() {
        return new String[]{"Name", "Login", "Password", "Is Admin"};
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

    public List<Book> getFavoriteBooks() {
        return favoriteBooks;
    }

    public void setFavoriteBooks(List<Book> favoriteBooks) {
        this.favoriteBooks = favoriteBooks;
    }

    public List<Book> getReadBooks() {
        return readBooks;
    }

    public void setReadBooks(List<Book> readBooks) {
        this.readBooks = readBooks;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return login;
    }
}
