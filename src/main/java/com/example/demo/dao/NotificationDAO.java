package com.example.demo.dao;

import com.example.demo.model.Notification;

import java.util.List;

public interface NotificationDAO {

    void addNotification(Notification notification) throws Exception;

    List<Notification> getUserNotifications(int userId) throws Exception;

    void markAsRead(int notificationId) throws Exception;
}