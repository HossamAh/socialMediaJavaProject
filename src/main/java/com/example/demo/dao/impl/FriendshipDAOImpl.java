package com.example.demo.dao.impl;

import com.example.demo.dao.FriendshipDAO;
import com.example.demo.model.Friendship;
import com.example.demo.Database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAOImpl implements FriendshipDAO {
    @Override
    public Friendship createFriendship(Friendship friendship) throws Exception {
        String sql = "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, friendship.getUserId());
            ps.setInt(2, friendship.getFriendId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                friendship.setId(rs.getInt(1));
            }
        }
        return friendship;
    }

    @Override
    public Friendship getFriendshipById(int id) throws Exception {
        String sql = "SELECT * FROM friendships WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractFriendship(rs);
            }
        }
        return null;
    }

    @Override
    public List<Friendship> getFriendsByUserId(int userId) throws Exception {
        List<Friendship> friendships = new ArrayList<>();
        String sql = "SELECT * FROM friendships WHERE user_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                friendships.add(extractFriendship(rs));
            }
        }
        return friendships;
    }

    @Override
    public boolean areFriends(int userId, int friendId) throws Exception {
        String sql = "SELECT * FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, friendId);
            ps.setInt(3, friendId);
            ps.setInt(4, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    @Override
    public boolean deleteFriendship(int id) throws Exception {
        String sql = "DELETE FROM friendships WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteFriendshipByUsers(int userId, int friendId) throws Exception {
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, friendId);
            ps.setInt(3, friendId);
            ps.setInt(4, userId);
            return ps.executeUpdate() > 0;
        }
    }

    private Friendship extractFriendship(ResultSet rs) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setId(rs.getInt("id"));
        friendship.setUserId(rs.getInt("user_id"));
        friendship.setFriendId(rs.getInt("friend_id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            friendship.setCreatedAt(ts.toLocalDateTime());
        }
        return friendship;
    }
}
