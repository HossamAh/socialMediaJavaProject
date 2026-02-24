package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import com.example.demo.model.Post;
import com.example.demo.Session;

public class NewsFeedController {
    @FXML
    private TextArea postTextArea;
    @FXML
    private ListView<Post> postsList;
    @FXML
    private Label imageLabel;

    private ObservableList<Post> posts = FXCollections.observableArrayList();
    private String selectedImagePath = null;

    @FXML
    public void initialize() {
        postsList.setItems(posts);
        postsList.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Post item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
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
            selectedImagePath = file.getAbsolutePath();
            imageLabel.setText(file.getName());
        } else {
            selectedImagePath = null;
            imageLabel.setText("No image selected");
        }
    }

    @FXML
    protected void onPost() {
        String text = postTextArea.getText().trim();
        if (!text.isEmpty() && Session.isLoggedIn()) {
            Post post = new Post(Session.getCurrentUser().getId(), text, "public");
            post.setAuthorUsername(Session.getCurrentUser().getUsername());
            if (selectedImagePath != null) {
                post.setImagePath(selectedImagePath);
            }
            posts.add(0, post);
            postTextArea.clear();
            selectedImagePath = null;
            imageLabel.setText("No image selected");
        }
    }
}
