package com.example.demo;

import com.example.demo.dao.CommentDAO;
import com.example.demo.dao.LikeDAO;
import com.example.demo.dao.NotificationDAO;
import com.example.demo.dao.PostDAO;
import com.example.demo.dao.impl.CommentDAOImpl;
import com.example.demo.dao.impl.LikeDAOImpl;
import com.example.demo.dao.impl.NotificationDAOImpl;
import com.example.demo.dao.impl.PostDAOImpl;
import com.example.demo.model.Comment;
import com.example.demo.model.Like;
import com.example.demo.model.Notification;
import com.example.demo.model.Post;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.net.URL;

public class NewsFeedController {

    /** Cached URL for PostCell.fxml so we resolve once and reuse (works from JAR and IDE). */
    private static URL postCellFxmlUrl;

    private static URL getPostCellFxmlUrl() {
        if (postCellFxmlUrl != null) return postCellFxmlUrl;
        ClassLoader cl = NewsFeedController.class.getClassLoader();
        String path = "com/example/demo/PostCell.fxml";
        postCellFxmlUrl = cl.getResource(path);
        if (postCellFxmlUrl != null) return postCellFxmlUrl;
        postCellFxmlUrl = Thread.currentThread().getContextClassLoader().getResource(path);
        if (postCellFxmlUrl != null) return postCellFxmlUrl;
        postCellFxmlUrl = NewsFeedController.class.getResource("/" + path);
        if (postCellFxmlUrl != null) return postCellFxmlUrl;
        postCellFxmlUrl = ClassLoader.getSystemResource(path);
        return postCellFxmlUrl;
    }
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
    private final LikeDAO likeDAO = new LikeDAOImpl();
    private final CommentDAO commentDAO = new CommentDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    private int currentPage = 0;
    private final int itemsPerPage = 5;
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
            // Add the new post cell at the top of the feed so it appears immediately
            try {
                VBox newCell = loadOrCreatePostCell(post);
                postsVBox.getChildren().add(0, newCell);
            } catch (Exception e) {
                postsVBox.getChildren().add(0, new Label(post.toString()));
            }

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
        isLoading = true;
        // Replace with fresh data from DB so we never duplicate when scrolling
        posts.setAll(postDAO.getPostsByUserId(Session.getCurrentUser().getId()));

        postsVBox.getChildren().clear();
        for (Post post : posts) {
            try {
                VBox postCell = loadOrCreatePostCell(post);
                postsVBox.getChildren().add(postCell);
            } catch (Exception e) {
                postsVBox.getChildren().add(new Label(post != null ? post.toString() : "?"));
            }
        }
        isLoading = false;
    }

    /** Load post cell from FXML when resource is available (JAR or IDE); otherwise build in code. */
    private VBox loadOrCreatePostCell(Post post) throws Exception {
        URL url = getPostCellFxmlUrl();
        if (url != null) {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(url);
            VBox cell = loader.load();
            PostCellController controller = loader.getController();
            if (controller != null) {
                controller.setPost(post);
            }
            return cell;
        }
        return createPostCell(post);
    }

    /** Build post cell in code when FXML is not on classpath (e.g. fallback). */
    private VBox createPostCell(Post post) throws Exception {
        VBox root = new VBox(6);
        root.setStyle("-fx-padding: 8; -fx-border-color: lightgray; -fx-border-width: 0 0 1 0;");
        root.setPadding(new Insets(6));

        Label contentLabel = new Label(post == null ? "" : post.toString());
        contentLabel.setWrapText(true);
        root.getChildren().add(contentLabel);

        ImageView postImage = new ImageView();
        postImage.setPreserveRatio(true);
        if (post != null && post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                postImage.setImage(new Image("file:" + post.getImagePath()));
                postImage.setFitWidth(300);
                postImage.setFitHeight(200);
            } catch (Exception ignored) { }
        } else {
            postImage.setFitWidth(0);
            postImage.setFitHeight(0);
        }
        root.getChildren().add(postImage);

        int likesCount = post != null ? likeDAO.getLikeCountByPostId(post.getId()) : 0;
        Label likesLabel = new Label(String.valueOf(likesCount));
        Button likeButton = new Button("Like");
        Button commentButton = new Button("Comment");
        HBox actions = new HBox(8, likeButton, likesLabel, commentButton);
        root.getChildren().add(actions);

        ListView<Comment> commentsList = new ListView<>();
        commentsList.setPrefHeight(80);
        ObservableList<Comment> comments = FXCollections.observableArrayList(
                post != null ? commentDAO.getCommentsByPostId(post.getId()) : java.util.Collections.emptyList());
        commentsList.setItems(comments);
        commentsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Comment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
                setGraphic(null);
            }
        });
        root.getChildren().add(commentsList);

        if (post != null && Session.isLoggedIn()) {
            likeButton.setOnAction(e -> {
                try {
                    likeDAO.createLike(new Like(Session.getCurrentUser().getId(), post.getId()));
                    // Notify post owner (unless they liked their own post)
                    if (post.getUserId() != Session.getCurrentUser().getId()) {
                        notificationDAO.addNotification(new Notification(post.getUserId(),
                                Session.getCurrentUser().getUsername() + " liked your post", "LIKE"));
                    }
                    likesLabel.setText(String.valueOf(likeDAO.getLikeCountByPostId(post.getId())));
                } catch (Exception ex) { throw new RuntimeException(ex); }
            });
            commentButton.setOnAction(e -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add Comment");
                dialog.setHeaderText(null);
                dialog.setContentText("Comment:");
                dialog.showAndWait().ifPresent(text -> {
                    try {
                        commentDAO.createComment(new Comment(Session.getCurrentUser().getId(), post.getId(), text));
                        // Notify post owner (unless they commented on their own post)
                        if (post.getUserId() != Session.getCurrentUser().getId()) {
                            notificationDAO.addNotification(new Notification(post.getUserId(),
                                    Session.getCurrentUser().getUsername() + " commented on your post", "COMMENT"));
                        }
                        comments.setAll(commentDAO.getCommentsByPostId(post.getId()));
                    } catch (Exception ex) { throw new RuntimeException(ex); }
                    });
            });
        }

        return root;
    }
}
