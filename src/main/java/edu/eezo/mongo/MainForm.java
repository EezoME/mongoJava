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

    private User loggedUser;
    private MongoController mongo;

    private boolean editMode = false;

    public MainForm(User user, MongoController mongoController) {
        super("LIBRARY");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        loggedUser = user;
        mongo = mongoController;
        initialize();
        setSize(260, 155);
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
                if (editMode) {
                    int row = table.getSelectedRow();
                    Book book = new Book(table.getValueAt(row, 0).toString(),
                            (Author) table.getValueAt(row, 1),
                            (int) table.getValueAt(row, 2),
                            table.getValueAt(row, 3).toString(),
                            (double) table.getValueAt(row, 4));
                    callBookGUI(book);
                } else {
                    callBookGUI(null);
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (table.getSelectedRowCount() == 1) {
                    editMode = true;
                    addNewBookButton.setText("Edit Sel. Book");
                    addNewBookButton.setToolTipText("Edit Selected Book");
                }
            }
        });

        table.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                editMode = false;
                addNewBookButton.setText("Add New Book");
                addNewBookButton.setToolTipText("or Change Selected");
            }
        });
    }

    private void showAllBooks() {
        java.util.List<Book> bookList = Book.makeListFromIterable(mongo.getAllDocuments("books"));
        Book.displayDataOnTable(table, bookList);
    }

    private void showAllAuthors() {
        java.util.List<Author> authorList = Author.makeListFromIterable(mongo.getAllDocuments("authors"));
        Author.displayDataOnTable(table, authorList);
    }

    private void callBookGUI(Book book) {
        BookGUI.main(book, mongo);
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
