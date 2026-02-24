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

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorText;

    public UserDAO userDAO = new UserDAOImpl();

    @FXML
    public void initialize() {
        // No scene switching here; scene/window may not be ready yet
    }

    @FXML
    protected void onLoginButtonClick() throws Exception {
        // simple authentication stub: accept any non-empty credentials
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        if (!user.isEmpty() && !pass.isEmpty()) {
            // set session user
            boolean check = userDAO.authenticate(user,pass);
            User userObj = userDAO.getUserByUsername(user);
            if (check){
                Session.setCurrentUser(userObj);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
                Scene scene = new Scene(loader.load(), 800, 600);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
            }
            else {
                errorText.setText("error in username or password");
            }

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
