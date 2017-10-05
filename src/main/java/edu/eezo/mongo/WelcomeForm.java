package edu.eezo.mongo;

import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonString;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by eezo33 on 04.10.2017.
 */
public class WelcomeForm extends JFrame {

    private JPanel rootPanel;
    private JButton asGuestButton;
    private JButton signUpButton;
    private JButton logInButton;
    private JPanel hiddenPanel;
    private JTextField textFieldLogin;
    private JTextField textFieldName;
    private JPasswordField passwordFieldLogIn;
    private JPasswordField passwordFieldConfirm;
    private JLabel labelName;
    private JLabel labelLogin;
    private JLabel labelPassword;
    private JLabel labelPasswordConfirm;
    private JLabel labelWarning;

    private boolean isPanelHidden = true;
    private MongoController mongo;

    public WelcomeForm() {
        super("LIBRARY");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setContentPane(rootPanel);
        initialize();
        setSize(260, 155);
        setLocationRelativeTo(null);
        setVisible(true);

        asGuestButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runMainForm(null);
            }
        });

        logInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isPanelHidden) {
                    showPanel(1);
                } else {
                    logIn();
                }
            }
        });

        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isPanelHidden) {
                    showPanel(2);
                } else {
                    signUp();
                }
            }
        });

        passwordFieldConfirm.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (checkConfirmPassword()) {
                    labelPasswordConfirm.setForeground(Color.GREEN);
                } else {
                    labelPasswordConfirm.setForeground(Color.RED);
                }
            }
        });
    }

    private void logIn() {
        if (!checkForUser(false)) {
            labelWarning.setText("Where is no user matched these login and password.");
            labelWarning.setVisible(true);
        } else {
            labelWarning.setVisible(false);
            runMainForm(User.makeInstanceFromDocument(mongo.getDocuments(new BsonDocument("login", new BsonString(textFieldLogin.getText()))).first()));
        }
    }

    private void signUp() {
        if (checkForUser(true)) {
            labelWarning.setText("A user with current login is already exists.");
            labelWarning.setVisible(true);
        } else {
            labelWarning.setVisible(false);
            if (checkConfirmPassword()) {
                User newUser = new User(textFieldName.getText(), textFieldLogin.getText(),
                        String.valueOf(passwordFieldLogIn.getPassword()));
                mongo.addDocument(newUser.generateDocument());
                runMainForm(newUser);
            }
        }
    }

    private boolean checkForUser(boolean checkOnlyLogin) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("login", textFieldLogin.getText());
        if (!checkOnlyLogin) {
            map.put("password", String.valueOf(passwordFieldLogIn.getPassword()));
        }
        return mongo.getDocuments(MongoController.getBsonFilterFromMap(map)).first() != null;
    }

    private void initialize() {
        hiddenPanel.setVisible(false);
        labelWarning.setVisible(false);
        mongo = MongoController.getDefaultInstance();
    }

    private void runMainForm(User user) {
        MainForm.main(user);
        this.setVisible(false);
    }

    /**
     * Shows hidden panel.
     *
     * @param pressedButtonCode 1 for 'Log In' button, 2 for 'Sign Up' button
     */
    private void showPanel(int pressedButtonCode) {
        isPanelHidden = false;
        hiddenPanel.setVisible(true);
        labelLogin.setVisible(true);
        labelPassword.setVisible(true);
        textFieldLogin.setVisible(true);
        passwordFieldLogIn.setVisible(true);
        asGuestButton.setEnabled(false);

        if (pressedButtonCode == 1) {
            setSize(340, 235);
            labelName.setVisible(false);
            labelPasswordConfirm.setVisible(false);
            textFieldName.setVisible(false);
            passwordFieldConfirm.setVisible(false);
            signUpButton.setEnabled(false);
        } else {
            setSize(320, 260);
            labelName.setVisible(true);
            labelPasswordConfirm.setVisible(true);
            textFieldName.setVisible(true);
            passwordFieldConfirm.setVisible(true);
            logInButton.setEnabled(false);
        }
    }

    private boolean checkConfirmPassword() {
        return Arrays.equals(passwordFieldLogIn.getPassword(), passwordFieldConfirm.getPassword());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WelcomeForm();
            }
        });
    }
}