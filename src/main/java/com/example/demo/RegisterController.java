package com.example.demo;

import com.example.demo.dao.UserDAO;
import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import at.favre.lib.crypto.bcrypt.BCrypt;

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
    protected void onRegisterButtonClick() {

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorText.setText("All fields are required");
            return;
        }

        // validation
        if (!password.equals(confirmPassword)) {
            errorText.setText("Passwords do not match");
            return;
        }

        try {

            String hashedPassword = BCrypt.withDefaults()
                    .hashToString(12, password.toCharArray());

            User user = new User(username, email, hashedPassword);

            userDAO.createUser(user);

            System.out.println("User registered successfully: " + username);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Scene scene = new Scene(loader.load(), 400, 300);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            errorText.setText("Error while creating user");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBackButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}