package com.example.demo.dao.impl;

import com.example.demo.Database.DBConnection;
import com.example.demo.dao.FriendDAO;
import com.example.demo.model.FriendRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAOImpl implements FriendDAO {

    @Override
    public void sendFriendRequest(int userId, int friendId) throws Exception {

        String sql = "INSERT INTO friends (user_id, friend_id, status) VALUES (?, ?, 'PENDING')";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, friendId);
            ps.executeUpdate();
        }
    }

    @Override
    public void acceptFriendRequest(int requestId) throws Exception {

        String sql = "UPDATE friends SET status='ACCEPTED' WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }

    @Override
    public void rejectFriendRequest(int requestId) throws Exception {

        String sql = "DELETE FROM friends WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, requestId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<String> getFriends(int userId) throws Exception {

        List<String> friends = new ArrayList<>();

        String sql =
                "SELECT u.username FROM users u " +
                        "JOIN friends f ON " +
                        "(u.id = f.friend_id AND f.user_id = ?) " +
                        "OR (u.id = f.user_id AND f.friend_id = ?) " +
                        "WHERE f.status = 'ACCEPTED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                friends.add(rs.getString("username"));
            }
        }

        return friends;
    }

    @Override
    public List<FriendRequest> getPendingRequests(int userId) throws Exception {

        List<FriendRequest> requests = new ArrayList<>();

        String sql =
                "SELECT f.id, u.username " +
                        "FROM friends f " +
                        "JOIN users u ON u.id = f.user_id " +
                        "WHERE f.friend_id=? AND f.status='PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int requestId = rs.getInt("id");
                String username = rs.getString("username");

                requests.add(new FriendRequest(requestId, username));
            }
        }

        return requests;
    }

    @Override
    public boolean areFriends(int user1, int user2) throws Exception {

        String sql = "SELECT * FROM friends WHERE " +
                "((user_id = ? AND friend_id = ?) OR " +
                "(user_id = ? AND friend_id = ?)) " +
                "AND status = 'ACCEPTED'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ps.setInt(3, user2);
            ps.setInt(4, user1);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    @Override
    public boolean isRequestPending(int user1, int user2) throws Exception {

        String sql = "SELECT * FROM friends WHERE " +
                "((user_id = ? AND friend_id = ?) OR " +
                "(user_id = ? AND friend_id = ?)) " +
                "AND status = 'PENDING'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user1);
            ps.setInt(2, user2);
            ps.setInt(3, user2);
            ps.setInt(4, user1);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }
}