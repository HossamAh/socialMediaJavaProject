package com.example.demo.dao.impl;

import com.example.demo.dao.NotificationDAO;
import com.example.demo.model.Notification;
import com.example.demo.Database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAOImpl implements NotificationDAO {
    @Override
    public Notification createNotification(Notification notification) throws Exception {
        String sql = "INSERT INTO notifications (user_id, type, content) VALUES (?, ?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getUserId());
            ps.setString(2, notification.getType());
            ps.setString(3, notification.getContent());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                notification.setId(rs.getInt(1));
            }
        }
        return notification;
    }

    @Override
    public Notification getNotificationById(int id) throws Exception {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractNotification(rs);
            }
        }
        return null;
    }

    @Override
    public List<Notification> getNotificationsByUserId(int userId) throws Exception {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotification(rs));
            }
        }
        return notifications;
    }

    @Override
    public Notification updateNotification(Notification notification) throws Exception {
        String sql = "UPDATE notifications SET type = ?, content = ? WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notification.getType());
            ps.setString(2, notification.getContent());
            ps.setInt(3, notification.getId());
            ps.executeUpdate();
        }
        return notification;
    }

    @Override
    public boolean deleteNotification(int id) throws Exception {
        String sql = "DELETE FROM notifications WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Notification extractNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setType(rs.getString("type"));
        notification.setContent(rs.getString("content"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            notification.setCreatedAt(ts.toLocalDateTime());
        }
        return notification;
    }
}
