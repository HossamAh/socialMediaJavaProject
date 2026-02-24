package com.example.demo;

import com.example.demo.dao.UserDAO;
import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Window;


public class ProfileController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField bioField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;

    
    @FXML
    private Label imageLabel;
    @FXML
    private ImageView profileImageView;

    private String selectedImagePath;
    
    private UserDAO userDAO = new UserDAOImpl();
    @FXML
    public void initialize() {
        if (Session.isLoggedIn()) {
            usernameField.setText(Session.getCurrentUser().getUsername());
            emailField.setText(Session.getCurrentUser().getEmail());
            bioField.setText(Session.getCurrentUser().getBio());
            imageLabel.setText(Session.getCurrentUser().getProfilePicture());
            if (Session.getCurrentUser().getProfilePicture() != null) {
                profileImageView.setImage(new javafx.scene.image.Image("file:" + Session.getCurrentUser().getProfilePicture()));
            }
        }
    }

    @FXML
    protected void onChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Window window = imageLabel.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            selectedImagePath = file.getPath();
            System.out.println(file);
            imageLabel.setText(file.getName());
            profileImageView.setImage(new javafx.scene.image.Image("file:" + selectedImagePath));
        } else {
            selectedImagePath = null;
            imageLabel.setText("No image selected");
        }
    }

    @FXML
    protected void onSaveProfile() throws Exception {
        // stub for saving profile changes
        User user = Session.getCurrentUser();
        user.setEmail(emailField.getText());
        user.setUsername(usernameField.getText());
        user.setBio(bioField.getText());
        user.setProfilePicture(selectedImagePath);
        userDAO.updateUser(user);
        System.out.println("Saving profile for " + usernameField.getText());
    }
}
