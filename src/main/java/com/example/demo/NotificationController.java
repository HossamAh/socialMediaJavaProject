package com.example.demo;

import com.example.demo.dao.NotificationDAO;
import com.example.demo.dao.impl.NotificationDAOImpl;
import com.example.demo.model.Notification;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.List;

public class NotificationController {

    @FXML
    private ListView<String> notificationList;

    private NotificationDAO notificationDAO = new NotificationDAOImpl();
    private List<Notification> notifications;

    @FXML
    public void initialize() {
        loadNotifications();
    }

    private void loadNotifications() {
        try {
            notifications = notificationDAO.getUserNotifications(Session.getCurrentUser().getId());

            notificationList.getItems().clear();

            for (Notification n : notifications) {
                notificationList.getItems().add(
                        (n.isRead() ? "" : "🔴 ") + n.getMessage()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMarkAsRead() {
        try {
            int index = notificationList.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                Notification selected = notifications.get(index);
                notificationDAO.markAsRead(selected.getId());
                loadNotifications();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}