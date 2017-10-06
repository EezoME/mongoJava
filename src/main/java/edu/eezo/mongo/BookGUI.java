package edu.eezo.mongo;

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

    private Book book;
    private MongoController mongo;

    public BookGUI(Book book, MongoController mongo) {
        setContentPane(contentPane);
        setModal(true);
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(buttonOK);
        this.book = book;
        this.mongo = mongo;
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
    }

    private void initialize() {
        initAuthorList();
        initSpinner();
        initSlider();

        if (book != null) {
            textFieldTitle.setText(book.getTitle());
            textFieldGenre.setText(book.getGenre());
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
        mongo.addDocument("books", book.generateDocument());
    }

    private void addNewBook() {
        book = new Book(
                textFieldTitle.getText(),
                null,
                (int) spinnerYear.getValue(),
                textFieldGenre.getText(),
                (double) sliderRating.getValue()
        );
        book.setAuthor(getSelectedAuthor());
        mongo.addDocument("books", book.generateDocument());
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

    static void main(Book book, MongoController mongo) {
        BookGUI dialog = new BookGUI(book, mongo);
        dialog.pack();
        dialog.setVisible(true);
    }
}
