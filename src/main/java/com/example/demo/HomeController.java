package com.example.demo;

import com.example.demo.dao.NotificationDAO;
import com.example.demo.dao.UserDAO;
import com.example.demo.dao.impl.FriendDAOImpl;
import com.example.demo.dao.impl.NotificationDAOImpl;
import com.example.demo.dao.impl.UserDAOImpl;
import com.example.demo.model.FriendRequest;
import com.example.demo.model.Notification;
import com.example.demo.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class HomeController {

    @FXML
    private ListView<String> friendsList;

    @FXML
    private ListView<FriendRequest> pendingList;
    @FXML
    private TextField friendIdField;

    private final FriendDAOImpl friendDAO = new FriendDAOImpl();

    @FXML
    public void initialize() {

        loadFriends();
        loadPending();

        friendsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FriendRequest selected = pendingList.getSelectionModel().getSelectedItem();
            }
        });

        friendsList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String username = friendsList.getSelectionModel().getSelectedItem();
                if (username != null) {
                    openFriendProfile(username);
                }
            }
        });
    }

    private void loadFriends() {
        try {
            int userId = Session.getCurrentUser().getId();
            List<String> friends = friendDAO.getFriends(userId);

            friendsList.getItems().clear();
            friendsList.getItems().addAll(friends);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPending() {
        try {
            int userId = Session.getCurrentUser().getId();
            List<FriendRequest> requests = friendDAO.getPendingRequests(userId);

            pendingList.getItems().clear();
            pendingList.getItems().addAll(requests);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddFriend() {
        try {

            int friendId = Integer.parseInt(friendIdField.getText());
            int currentUserId = Session.getCurrentUser().getId();

            // إرسال طلب الصداقة
            friendDAO.sendFriendRequest(currentUserId, friendId);

            // 👇 إنشاء Notification للمستخدم اللي استلم الطلب
            NotificationDAO notificationDAO = new NotificationDAOImpl();

            Notification notification = new Notification(
                    friendId,
                    Session.getCurrentUser().getUsername() + " sent you a friend request",
                    "FRIEND_REQUEST"
            );

            notificationDAO.addNotification(notification);

            loadPending();

            System.out.println("Friend request sent + notification created");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAccept() {
        try {
            FriendRequest selected = pendingList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                friendDAO.acceptFriendRequest(selected.getId());
                loadPending();
                loadFriends();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReject() {
        try {
            FriendRequest selected = pendingList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                friendDAO.rejectFriendRequest(selected.getId());
                loadPending();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openFriendProfile(String username) {
        try {

            UserDAO userDAO = new UserDAOImpl();
            User friend = userDAO.getUserByUsername(username);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("profile.fxml"));

            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(friend);

            Stage stage = new Stage();
            stage.setTitle(friend.getUsername() + " Profile");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}