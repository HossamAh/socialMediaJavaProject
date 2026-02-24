package com.example.demo;

import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.demo.dao.UserDAO;
import com.example.demo.model.User;
import java.io.IOException;

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorText;


    private UserDAO userDAO = new UserDAOImpl();

    @FXML
    protected void onRegisterButtonClick() throws IOException {
        // rudimentary registration: create user and return to login
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        if (!username.isEmpty() && pass.equals(confirm)) {
            // in real app persist user
            User user =new User(username,email,pass);
            try {
                user = userDAO.createUser(user);
                Session.setCurrentUser(user);
            } catch (Exception e) {
                errorText.setText("error in sign up new user");
                throw new RuntimeException(e);
            }
            System.out.println("Registered user: " + username);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}
