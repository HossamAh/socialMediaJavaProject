package com.example.demo;

import com.example.demo.dao.UserDAO;
import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.User;
import at.favre.lib.crypto.bcrypt.BCrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorText;

    private final UserDAO userDAO = new UserDAOImpl();

    @FXML
    protected void onLoginButtonClick() throws IOException {

        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // validation
        if (username.isEmpty() || password.isEmpty()) {
            errorText.setText("Please enter username and password");
            return;
        }

        try {
            User user = userDAO.getUserByUsername(username);

            if (user == null) {
                errorText.setText("Invalid username or password");
                return;
            }

            BCrypt.Result result = BCrypt.verifyer()
                    .verify(password.toCharArray(), user.getPassword());

            if (result.verified) {

                Session.setCurrentUser(user);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Scene scene = new Scene(loader.load(), 800, 600);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);

            } else {
                errorText.setText("Invalid username or password");
            }

        } catch (Exception e) {
            errorText.setText("Login error");
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("register.fxml"));
        Scene scene = new Scene(loader.load(), 400, 300);
        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
    }
}