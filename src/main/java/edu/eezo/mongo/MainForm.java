package edu.eezo.mongo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by eezo33 on 05.10.2017.
 */
public class MainForm extends JFrame {
    private JPanel rootPanel;
    private JTable table;
    private JLabel labelLogin;
    private JButton showAllBooksButton;
    private JButton showAllAuthorsButton;
    private JButton showMyFavoriteBooksButton;
    private JButton showAllMyBooksButton;
    private JButton addNewBookButton;
    private JButton updateSelRowButton;
    private JButton allRowsButton;
    private JButton showAllUsersButton;
    private JPanel adminPanel;
    private JPanel simpleUserPanel;
    private JButton editSelectedBookButton;

    private User loggedUser;
    private MongoController mongo;

    private boolean editMode = false;
    private boolean isBooksOnTable = false;

    public MainForm(User user, MongoController mongoController) {
        super("LIBRARY");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        loggedUser = user;
        mongo = mongoController;
        initialize();
        setSize(540, 380);
        setLocationRelativeTo(null);
        setVisible(true);

        showAllBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllBooks();
            }
        });

        showAllAuthorsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllAuthors();
            }
        });

        addNewBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                callBookGUI(null);
            }
        });

        editSelectedBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row == -1) {
                    editSelectedBookButton.setEnabled(false);
                    return;
                }

                callBookGUI(new Book(
                        table.getValueAt(row, 0).toString(),
                        (Author) table.getValueAt(row, 1),
                        (int) table.getValueAt(row, 2),
                        table.getValueAt(row, 3).toString(),
                        (double) table.getValueAt(row, 4)
                ));
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (isBooksOnTable) {
                    editMode = table.getSelectedRowCount() == 1;
                    editSelectedBookButton.setEnabled(editMode);
                }
            }
        });

        showAllMyBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllMyBooks();
            }
        });

        showMyFavoriteBooksButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMyFavoriteBooks();
            }
        });
    }

    private void showAllBooks() {
        java.util.List<Book> bookList = Book.makeListFromIterable(mongo.getAllDocuments("books"));
        Book.displayDataOnTable(table, bookList);
        isBooksOnTable = true;
    }

    private void showAllMyBooks() {
        java.util.List<Book> bookList = Book.filterListWithBookTitleList(Book.makeListFromIterable(mongo.getAllDocuments("books")),
                loggedUser.getReadBooks());
        Book.displayDataOnTable(table, bookList);
        isBooksOnTable = true;
    }

    private void showMyFavoriteBooks() {
        java.util.List<Book> bookList = Book.filterListWithBookTitleList(Book.makeListFromIterable(mongo.getAllDocuments("books")),
                loggedUser.getFavoriteBooks());
        Book.displayDataOnTable(table, bookList);
        isBooksOnTable = true;
    }

    private void showAllAuthors() {
        java.util.List<Author> authorList = Author.makeListFromIterable(mongo.getAllDocuments("authors"));
        Author.displayDataOnTable(table, authorList);
        isBooksOnTable = false;
    }

    private void callBookGUI(Book book) {
        BookGUI.main(book, mongo, loggedUser);
    }


    private void initialize() {
        if (loggedUser == null) {
            makeGuestView();
            labelLogin.setText("You logged in as guest.");
        } else if (!loggedUser.isAdmin()) {
            makeSimpleUserView();
            labelLogin.setText("You logged in as " + loggedUser.getName());
        } else {
            labelLogin.setText("You logged in as " + loggedUser.getName() + " (privileged user)");
        }

        editSelectedBookButton.setEnabled(false);
    }

    private void makeGuestView() {
        makeSimpleUserView();
        simpleUserPanel.setVisible(false);
    }

    private void makeSimpleUserView() {
        adminPanel.setVisible(false);
    }

    public static void main(final User user, final MongoController mongo) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm(user, mongo);
            }
        });
    }
}
