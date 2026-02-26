package com.example.demo;

import com.example.demo.dao.PostDAO;
import com.example.demo.dao.impl.PostDAOImpl;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;


import com.example.demo.model.Post;
import com.example.demo.Session;

public class NewsFeedController {
    @FXML
    private TextArea postTextArea;
    @FXML
    private VBox postsVBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Label imageLabel;

    @FXML
    private ImageView postImageViewer;

    @FXML
    private ComboBox privacyComboBox;

    private ObservableList<Post> posts = FXCollections.observableArrayList();
    private String selectedImagePath = null;
    private String privacyValue;
    private final PostDAO postDAO = new PostDAOImpl();

    private int currentPage = 0;
    private final int itemsPerPage = 2;
    private boolean isLoading = false;

    @FXML
    public void initialize() throws Exception {
        privacyComboBox.getItems().addAll("public", "friends", "private");
        privacyValue="public";
        privacyComboBox.setValue(privacyValue);
        privacyComboBox.setOnAction(actionEvent -> {
            privacyValue = privacyComboBox.getValue().toString();
        });
        // Load initial data
        loadMorePosts();
        // Add a listener to the vertical scroll position (vvalue)
        scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // Check if the user has scrolled to the bottom (or very near it)
                if (newValue.doubleValue() >= 0.95 && !isLoading) { // Use a threshold like 0.95
                    try {
                        loadMorePosts();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
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
            selectedImagePath = file.getPath();
            imageLabel.setText(file.getName());
            postImageViewer.setImage(new javafx.scene.image.Image("file:" +selectedImagePath));
            postImageViewer.setFitWidth(200);
            postImageViewer.setFitHeight(200);
        } else {
            selectedImagePath = null;
            imageLabel.setText("No image selected");
            postImageViewer.setFitWidth(0);
            postImageViewer.setFitHeight(0);

        }
    }

    @FXML
    protected void onPost() throws Exception {
        String text = postTextArea.getText().trim();
        if (!text.isEmpty() && Session.isLoggedIn()) {
            Post post = new Post(Session.getCurrentUser().getId(), text, privacyValue);
            post.setAuthorUsername(Session.getCurrentUser().getUsername());
            if (selectedImagePath != null) {
                post.setImagePath(selectedImagePath);
            }
            post = postDAO.createPost(post);

            posts.add(0, post);
            postTextArea.clear();
            selectedImagePath = null;
            imageLabel.setText("No image selected");
            postImageViewer.setImage(null);
            postImageViewer.setFitWidth(0);
            postImageViewer.setFitHeight(0);
        }
    }

    private void refreshPosts() throws Exception {
        posts.setAll(postDAO.getPostsByUserId(Session.getCurrentUser().getId()));
    }

    private void loadMorePosts() throws Exception {
        // Implement pagination logic here to load more posts when the user scrolls to the bottom
        isLoading = true;
        posts.addAll(postDAO.getPostsByUserId(Session.getCurrentUser().getId()));
        
        postsVBox.getChildren().clear();
        for (Post post : posts) {
            try {
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/example/demo/PostCell.fxml"));
                javafx.scene.layout.VBox postCell = loader.load();
                PostCellController controller = loader.getController();
                controller.setPost(post);
                postsVBox.getChildren().add(postCell);
            } catch (java.io.IOException ex) {
                Label errorLabel = new Label(post.toString());
                postsVBox.getChildren().add(errorLabel);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        currentPage++;
        isLoading = false;
    }
}
