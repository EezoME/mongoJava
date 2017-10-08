package edu.eezo.mongo.view;

import edu.eezo.mongo.controller.MongoController;
import edu.eezo.mongo.model.Author;
import edu.eezo.mongo.model.Book;
import edu.eezo.mongo.model.User;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class BookGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldTitle;
    private JComboBox comboBoxAuthor;
    private JSpinner spinnerYear;
    private JTextField textFieldGenre;
    private JSlider sliderRating;
    private JCheckBox addToMyBooksCheckBox;
    private JCheckBox markAsFavoriteCheckBox;

    private Book book;
    private MongoController mongo;
    private User user;

    private String oldTitle = null;

    public BookGUI(Book book, MongoController mongo, User user) {
        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);
        this.book = book;
        this.mongo = mongo;
        this.user = user;
        initialize();

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        addToMyBooksCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAsFavoriteCheckBox.setEnabled(addToMyBooksCheckBox.isSelected());
            }
        });
    }

    private void initialize() {
        initAuthorList();
        initSpinner();
        initSlider();

        if (book != null) {
            textFieldTitle.setText(book.getTitle());
            textFieldGenre.setText(book.getGenre());
            addToMyBooksCheckBox.setSelected(user.isInReadBookList(book));
            markAsFavoriteCheckBox.setSelected(user.isInFavoriteBookList(book));
            markAsFavoriteCheckBox.setEnabled(addToMyBooksCheckBox.isSelected());
            oldTitle = book.getTitle();
        }
    }

    private void initAuthorList() {
        comboBoxAuthor.removeAllItems();
        List<Author> authorList = Author.makeListFromIterable(mongo.getAllDocuments("authors"));
        for (int i = 0, length = authorList.size(); i < length; i++) {
            comboBoxAuthor.addItem(authorList.get(i));
        }
        if (book != null) {
            comboBoxAuthor.setSelectedItem(book.getAuthor());
        }
    }

    private void initSpinner() {
        SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel();
        spinnerNumberModel.setMinimum(1600);
        spinnerNumberModel.setMaximum(2020);
        spinnerNumberModel.setValue(1980);
        spinnerNumberModel.setStepSize(1);
        if (book != null) {
            spinnerNumberModel.setValue(book.getYear());
        }
        spinnerYear.setModel(spinnerNumberModel);
    }

    private void initSlider() {
        sliderRating.setMinimum(1);
        sliderRating.setMaximum(5);
        if (book != null) {
            sliderRating.setValue((int) book.getRating());
        }
    }


    private void onOK() {
        if (book == null) {
            addNewBook();
        } else {
            saveChanges();
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    private void saveChanges() {
        book.setTitle(textFieldTitle.getText());
        book.setAuthor(getSelectedAuthor());
        book.setYear((int) spinnerYear.getValue());
        book.setGenre(textFieldGenre.getText());
        book.setRating((double) sliderRating.getValue());
        mongo.replaceDocument("books", MongoController.getBsonFilterFormEnumeration("title", oldTitle), book.generateDocument());
        handleCheckboxes();
    }

    private void addNewBook() {
        book = new Book(
                textFieldTitle.getText(),
                null, // author sets later, bc new author needs a book object
                (int) spinnerYear.getValue(),
                textFieldGenre.getText(),
                (double) sliderRating.getValue()
        );
        book.setAuthor(getSelectedAuthor());
        mongo.addDocument("books", book.generateDocument());
        handleCheckboxes();
    }

    private Author getSelectedAuthor() {
        Author author = null;
        for (int i = 0, length = comboBoxAuthor.getItemCount(); i < length; i++) {
            if (comboBoxAuthor.getSelectedItem().equals(comboBoxAuthor.getItemAt(i))) {
                author = (Author) comboBoxAuthor.getItemAt(i);
            }
        }

        if (author == null) {
            List<String> books = new ArrayList<>();
            books.add(book.toString());
            Author newAuthor = new Author(comboBoxAuthor.getSelectedItem().toString(), books);
            mongo.addDocument("authors", newAuthor.generateDocument());
            return newAuthor;
        } else {
            return author;
        }
    }

    private void handleCheckboxes() {
        if (addToMyBooksCheckBox.isSelected()) {
            user.addToReadBooks(book);

            if (markAsFavoriteCheckBox.isSelected()) {
                user.addToFavoriteBooks(book);
            } else {
                user.removeFromFavoriteBooks(book);
            }

            mongo.replaceDocument("users", MongoController.getBsonFilterFormEnumeration("login", user.getLogin()), user.generateDocument());
        } else {
            user.removeFromReadBooks(book);
            user.removeFromFavoriteBooks(book);
        }
    }

    static void main(Book book, MongoController mongo, User user) {
        BookGUI dialog = new BookGUI(book, mongo, user);
        dialog.pack();
        dialog.setVisible(true);
    }
}
