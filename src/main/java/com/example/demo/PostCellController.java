package com.example.demo;

import com.example.demo.dao.CommentDAO;
import com.example.demo.dao.LikeDAO;
import com.example.demo.dao.NotificationDAO;
import com.example.demo.dao.impl.CommentDAOImpl;
import com.example.demo.dao.impl.LikeDAOImpl;
import com.example.demo.dao.impl.NotificationDAOImpl;
import com.example.demo.model.Comment;
import com.example.demo.model.Like;
import com.example.demo.model.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import com.example.demo.model.Post;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class PostCellController {
    @FXML
    private Label contentLabel;
    @FXML
    private Label likesLabel;
    @FXML
    private Button likeButton;
    @FXML
    private Button commentButton;

    @FXML
    private ImageView postImage;

    @FXML
    private ListView<Comment> commentsList;
    private ObservableList<Comment> comments = FXCollections.observableArrayList();

    private Post post;
    private int likes = 0;
    private final LikeDAO likeDAO = new LikeDAOImpl();
    private final CommentDAO commentDAO = new CommentDAOImpl();
    private final NotificationDAO notificationDAO = new NotificationDAOImpl();

    public void setPost(Post post) throws Exception {
        this.post = post;
        contentLabel.setText(post == null ? "" : post.toString());
        // initialise likes (UI-only). Replace with real model value if Post has one.
        assert post != null;
        likes = likeDAO.getLikeCountByPostId(post.getId());
        likesLabel.setText(String.valueOf(likes));

        comments.setAll(commentDAO.getCommentsByPostId(post.getId()));
        commentsList.setItems(comments);
        commentsList.setCellFactory(lv -> new javafx.scene.control.ListCell<Comment>() {
            @Override
            protected void updateItem(Comment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.toString());
                    setGraphic(null);
                }
            }
        });

        likeButton.setOnAction(e -> {

            Like newLike = new Like(Session.getCurrentUser().getId(), post.getId());
            try {
                likeDAO.createLike(newLike);
                if (post.getUserId() != Session.getCurrentUser().getId()) {
                    Notification notification = new Notification(post.getUserId(),
                            Session.getCurrentUser().getUsername() + " liked your post", "LIKE");
                    notificationDAO.addNotification(notification);
                }
                likes++;
                likesLabel.setText(String.valueOf(likes));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        commentButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Comment");
            dialog.setHeaderText(null);
            dialog.setContentText("Comment:");
            dialog.showAndWait().ifPresent(comment -> {
                Comment newComment = new Comment(Session.getCurrentUser().getId(), post.getId(), comment);
                try {
                    commentDAO.createComment(newComment);
                    if (post.getUserId() != Session.getCurrentUser().getId()) {
                        Notification notification = new Notification(post.getUserId(),
                                Session.getCurrentUser().getUsername() + " commented on your post", "COMMENT");
                        notificationDAO.addNotification(notification);
                    }
                    comments.setAll(commentDAO.getCommentsByPostId(post.getId()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        });
    }
}