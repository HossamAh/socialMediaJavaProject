package com.example.demo.dao.impl;

import com.example.demo.dao.FriendRequestDAO;
import com.example.demo.model.FriendRequest;
import com.example.demo.Database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendRequestDAOImpl implements FriendRequestDAO {
    @Override
    public FriendRequest createFriendRequest(FriendRequest friendRequest) throws Exception {
        String sql = "INSERT INTO friend_requests (sender_id, receiver_id) VALUES (?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, friendRequest.getSenderId());
            ps.setInt(2, friendRequest.getReceiverId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                friendRequest.setId(rs.getInt(1));
            }
        }
        return friendRequest;
    }

    @Override
    public FriendRequest getFriendRequestById(int id) throws Exception {
        String sql = "SELECT * FROM friend_requests WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractFriendRequest(rs);
            }
        }
        return null;
    }

    @Override
    public List<FriendRequest> getReceivedRequests(int receiverId) throws Exception {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM friend_requests WHERE receiver_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(extractFriendRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public List<FriendRequest> getSentRequests(int senderId) throws Exception {
        List<FriendRequest> requests = new ArrayList<>();
        String sql = "SELECT * FROM friend_requests WHERE sender_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                requests.add(extractFriendRequest(rs));
            }
        }
        return requests;
    }

    @Override
    public boolean deleteFriendRequest(int id) throws Exception {
        String sql = "DELETE FROM friend_requests WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean existsRequest(int senderId, int receiverId) throws Exception {
        String sql = "SELECT * FROM friend_requests WHERE sender_id = ? AND receiver_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    private FriendRequest extractFriendRequest(ResultSet rs) throws SQLException {
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setId(rs.getInt("id"));
        friendRequest.setSenderId(rs.getInt("sender_id"));
        friendRequest.setReceiverId(rs.getInt("receiver_id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            friendRequest.setCreatedAt(ts.toLocalDateTime());
        }
        return friendRequest;
    }
}
