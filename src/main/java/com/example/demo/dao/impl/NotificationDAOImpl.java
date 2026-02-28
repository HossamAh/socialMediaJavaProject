package com.example.demo.dao.impl;

import com.example.demo.Database.DBConnection;
import com.example.demo.dao.NotificationDAO;
import com.example.demo.model.Notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {

    @Override
    public void addNotification(Notification notification) throws Exception {
        System.out.println("Adding notification: " + notification.getMessage());
        String sql = "INSERT INTO notifications (user_id, message, type) VALUES (?, ?, ?)";

        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getMessage());
            ps.setString(3, notification.getType());

            ps.executeUpdate();
        }
    }

    @Override
    public List<Notification> getUserNotifications(int userId) throws Exception {

        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";

        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Notification n = new Notification();
                n.setId(rs.getInt("id"));
                n.setUserId(rs.getInt("user_id"));
                n.setMessage(rs.getString("message"));
                n.setType(rs.getString("type"));
                n.setRead(rs.getBoolean("is_read"));
                n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

                list.add(n);
            }
        }
        return list;
    }

    @Override
    public void markAsRead(int notificationId) throws Exception {

        String sql = "UPDATE notifications SET is_read = true WHERE id = ?";

        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }
    }
}