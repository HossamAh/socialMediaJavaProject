package com.example.demo.dao;

import com.example.demo.model.Notification;
import java.util.List;

public interface NotificationDAO {
    Notification createNotification(Notification notification) throws Exception;
    Notification getNotificationById(int id) throws Exception;
    List<Notification> getNotificationsByUserId(int userId) throws Exception;
    Notification updateNotification(Notification notification) throws Exception;
    boolean deleteNotification(int id) throws Exception;
}
