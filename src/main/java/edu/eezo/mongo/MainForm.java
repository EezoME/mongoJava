package edu.eezo.mongo;

import javax.swing.*;
import java.awt.*;

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

    public MainForm(){
        super("LIBRARY");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        setSize(260, 155);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(final User user) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainForm();
                System.out.println(user);
            }
        });
    }
}
