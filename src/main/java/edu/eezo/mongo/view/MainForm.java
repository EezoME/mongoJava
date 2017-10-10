package edu.eezo.mongo.view;

import edu.eezo.mongo.controller.MongoController;
import edu.eezo.mongo.model.Author;
import edu.eezo.mongo.model.Book;
import edu.eezo.mongo.model.User;
import org.bson.conversions.Bson;

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
    private JButton deleteSelectedRowButton;

    private User loggedUser;
    private MongoController mongo;

    private boolean editMode = false;
    private boolean isBooksOnTable = false;

    public MainForm(User user, MongoController mongoController) {
        super("LIB-APP");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        loggedUser = user;
        mongo = mongoController;
        initialize();
        setSize(685, 410);
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
                    deleteSelectedRowButton.setEnabled(false);
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
                    deleteSelectedRowButton.setEnabled(editMode);
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

        showAllUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAllUsers();
            }
        });

        updateSelRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRows(false);
            }
        });

        allRowsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateRows(true);
            }
        });

        deleteSelectedRowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (table.getSelectedRow() == -1) {
                    deleteSelectedRowButton.setEnabled(false);
                    editSelectedBookButton.setEnabled(false);
                    return;
                }
                deleteSelectedRow();
            }
        });
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
        deleteSelectedRowButton.setEnabled(false);
    }

    private void showAllBooks() {
        java.util.List<Book> bookList = Book.makeListFromIterable(mongo.getAllDocuments("books"));
        Book.displayDataOnTable(table, bookList);
        isBooksOnTable = true;
        table.setToolTipText("Books");
    }

    private void showAllMyBooks() {
        java.util.List<Book> bookList = Book.filterListWithBookTitleList(Book.makeListFromIterable(mongo.getAllDocuments("books")),
                loggedUser.getReadBooks());
        Book.displayDataOnTable(table, bookList);
        isBooksOnTable = true;
        table.setToolTipText("Books");
    }

    private void showMyFavoriteBooks() {
        java.util.List<Book> bookList = Book.filterListWithBookTitleList(Book.makeListFromIterable(mongo.getAllDocuments("books")),
                loggedUser.getFavoriteBooks());
        Book.displayDataOnTable(table, bookList);
        isBooksOnTable = true;
        table.setToolTipText("Books");
    }

    private void showAllAuthors() {
        java.util.List<Author> authorList = Author.makeListFromIterable(mongo.getAllDocuments("authors"));
        Author.displayDataOnTable(table, authorList);
        isBooksOnTable = false;
        table.setToolTipText("Authors");
    }

    private void showAllUsers() {
        java.util.List<User> userList = User.makeListFromIterable(mongo.getAllDocuments("users"));
        User.displayDataOnTable(table, userList);
        isBooksOnTable = false;
        table.setToolTipText("Users");
    }

    private void updateRows(boolean all) {
        if (table.getSelectedRowCount() < 1) {
            return;
        }

        switch (table.getToolTipText()) {
            case "Books":
                if (all) {
                    updateAllBookRows();
                } else {
                    updateBookRows();
                }
                break;
            case "Authors":
                if (all) {
                    updateAllAuthorRows();
                } else {
                    updateAuthorRows();
                }
                break;
            case "Users":
                if (all) {
                    updateAllUserRows();
                } else {
                    updateUserRows();
                }
                break;
        }
    }

    private void updateBookRows() {
        int[] rows = table.getSelectedRows();
        for (int row : rows) {
            updateSingleBookRow(row);
        }
    }

    private void updateAllBookRows() {
        for (int i = 0; i < table.getRowCount(); i++) {
            updateSingleBookRow(i);
        }
    }

    private void updateSingleBookRow(int row) {
        mongo.replaceDocument(
                "books",
                MongoController.getBsonFilterFormEnumeration("title", table.getValueAt(row, 0).toString()),
                new Book(
                        table.getValueAt(row, 0).toString(),
                        Author.getInstanceFromTableCell(table.getValueAt(row, 1), table.getValueAt(row, 0)),
                        (int) table.getValueAt(row, 2),
                        table.getValueAt(row, 3).toString(),
                        Double.parseDouble(table.getValueAt(row, 4).toString())
                ).generateDocument()
        );
    }

    private void updateAuthorRows() {
        int[] rows = table.getSelectedRows();
        for (int row : rows) {
            updateSingleAuthorRow(row);
        }
    }

    private void updateAllAuthorRows() {
        for (int i = 0; i < table.getRowCount(); i++) {
            updateSingleAuthorRow(i);
        }
    }

    private void updateSingleAuthorRow(int row) {
        mongo.replaceDocument(
                "authors",
                MongoController.getBsonFilterFormEnumeration("name", table.getValueAt(row, 0).toString()),
                new Author(
                        (String) table.getValueAt(row, 0),
                        User.parseTableCell(table.getValueAt(row, 1))
                ).generateDocument()
        );
    }

    private void updateUserRows() {
        int[] rows = table.getSelectedRows();
        for (int row : rows) {
            updateSingleUserRow(row);
        }
    }

    private void updateAllUserRows() {
        for (int i = 0; i < table.getRowCount(); i++) {
            updateSingleUserRow(i);
        }
    }

    private void updateSingleUserRow(int row) {
        mongo.replaceDocument(
                "users",
                MongoController.getBsonFilterFormEnumeration("login", table.getValueAt(row, 1).toString()),
                new User(
                        (String) table.getValueAt(row, 0),
                        (String) table.getValueAt(row, 1),
                        (String) table.getValueAt(row, 2),
                        (String) table.getValueAt(row, 3),
                        User.parseTableCell(table.getValueAt(row, 4)),
                        User.parseTableCell(table.getValueAt(row, 5))
                ).generateDocument()
        );
    }

    private void deleteSelectedRow() {
        String collection = table.getToolTipText().toLowerCase();
        Bson filter = null;
        switch (collection) {
            case "books":
                filter = MongoController.getBsonFilterFormEnumeration("title", table.getValueAt(table.getSelectedRow(), 0).toString());
                break;
            case "authors":
                filter = MongoController.getBsonFilterFormEnumeration("name", table.getValueAt(table.getSelectedRow(), 0).toString());
                break;
            case "users":
                filter = MongoController.getBsonFilterFormEnumeration("login", table.getValueAt(table.getSelectedRow(), 1).toString());
                break;
        }
        if (filter == null) {
            return;
        }
        ((DefaultTableModel) table.getModel()).removeRow(table.getSelectedRow());
        mongo.deleteDocument(collection, filter);
    }

    private void callBookGUI(Book book) {
        BookGUI.main(book, mongo, loggedUser);
    }

    private void makeGuestView() {
        makeSimpleUserView();
        simpleUserPanel.setVisible(false);
    }

    private void makeSimpleUserView() {
        adminPanel.setVisible(false);
    }

    static void main(final User user, final MongoController mongo) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm(user, mongo);
            }
        });
    }
}
