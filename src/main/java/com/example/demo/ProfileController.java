package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ProfileController {
    @FXML
    private TextField usernameField;

    @FXML
    public void initialize() {
        if (Session.isLoggedIn()) {
            usernameField.setText(Session.getCurrentUser().getUsername());
        }
    }

    @FXML
    protected void onSaveProfile() {
        // stub for saving profile changes
        System.out.println("Saving profile for " + usernameField.getText());
    }
}
