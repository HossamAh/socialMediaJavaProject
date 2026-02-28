package com.example.demo;

import com.example.demo.dao.FriendDAO;
import com.example.demo.dao.NotificationDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.dao.impl.FriendDAOImpl;
import com.example.demo.dao.impl.NotificationDAOImpl;
import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.util.List;

public class SearchController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<User> resultsList;

    private UserDAO userDAO = new UserDAOImpl();
    private FriendDAO friendDAO = new FriendDAOImpl();
    private NotificationDAO notificationDAO = new NotificationDAOImpl();

    @FXML
    public void initialize() {

        resultsList.setCellFactory(param -> new ListCell<>() {

            private final Button actionButton = new Button();
            private final HBox container = new HBox(10);
            private final Label nameLabel = new Label();

            {
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                container.getChildren().addAll(nameLabel, actionButton);
            }

            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {
                    setGraphic(null);
                } else {

                    nameLabel.setText(user.getUsername() + " (" + user.getEmail() + ")");

                    int currentUserId = Session.getCurrentUser().getId();

                    try {

                        if (user.getId() == currentUserId) {
                            actionButton.setVisible(false);
                        }

                        else if (friendDAO.areFriends(currentUserId, user.getId())) {
                            actionButton.setVisible(true);
                            actionButton.setText("Friends");
                            actionButton.setDisable(true);
                        }

                        else if (friendDAO.isRequestPending(currentUserId, user.getId())) {
                            actionButton.setVisible(true);
                            actionButton.setText("Pending");
                            actionButton.setDisable(true);
                        }

                        else {

                            actionButton.setVisible(true);
                            actionButton.setText("Add Friend");
                            actionButton.setDisable(false);

                            actionButton.setOnAction(e -> {
                                try {

                                    friendDAO.sendFriendRequest(
                                            currentUserId,
                                            user.getId()
                                    );

                                    Notification notification = new Notification(
                                            user.getId(),
                                            Session.getCurrentUser().getUsername()
                                                    + " sent you a friend request",
                                            "FRIEND_REQUEST"
                                    );

                                    notificationDAO.addNotification(notification);

                                    actionButton.setText("Pending");
                                    actionButton.setDisable(true);

                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            });
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    setGraphic(container);
                }
            }
        });

        resultsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                User selectedUser = resultsList.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    openUserProfile(selectedUser);
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        try {

            String keyword = searchField.getText();

            List<User> users = userDAO.searchUsers(keyword);

            resultsList.getItems().clear();
            resultsList.getItems().addAll(users);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openUserProfile(User user) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle(user.getUsername() + " Profile");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}