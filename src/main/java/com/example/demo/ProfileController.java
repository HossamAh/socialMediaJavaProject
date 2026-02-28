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
    private Label imageLabel;
    @FXML
    private ImageView profileImageView;

    private String selectedImagePath;

    private UserDAO userDAO = new UserDAOImpl();
    private User user;

    @FXML
    public void initialize() {
        if (user == null && Session.isLoggedIn()) {
            setUser(Session.getCurrentUser());
        }
    }

    public void setUser(User user) {
        this.user = user;
        loadUserData();

        if (user.getId() != Session.getCurrentUser().getId()) {
            usernameField.setEditable(false);
            emailField.setEditable(false);
            bioField.setEditable(false);
        }
    }

    private void loadUserData() {
        if (user != null) {
            usernameField.setText(user.getUsername());
            emailField.setText(user.getEmail());
            bioField.setText(user.getBio());
            imageLabel.setText(user.getProfilePicture());

            if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                profileImageView.setImage(
                        new javafx.scene.image.Image("file:" + user.getProfilePicture())
                );
            }
        }
    }

    @FXML
    protected void onChooseImage() {
        if (user.getId() != Session.getCurrentUser().getId()) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Window window = imageLabel.getScene().getWindow();
        java.io.File file = fileChooser.showOpenDialog(window);

        if (file != null) {
            selectedImagePath = file.getPath();
            imageLabel.setText(file.getName());
            profileImageView.setImage(
                    new javafx.scene.image.Image("file:" + selectedImagePath)
            );
        }
    }

    @FXML
    protected void onSaveProfile() throws Exception {

        if (user.getId() != Session.getCurrentUser().getId()) {
            return;
        }

        user.setEmail(emailField.getText());
        user.setUsername(usernameField.getText());
        user.setBio(bioField.getText());

        if (selectedImagePath != null) {
            user.setProfilePicture(selectedImagePath);
        }

        userDAO.updateUser(user);

        System.out.println("Profile Updated");
    }
}