package com.example.demo.dao.impl;

import com.example.demo.dao.LikeDAO;
import com.example.demo.model.Like;
import com.example.demo.Database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LikeDAOImpl implements LikeDAO {
    @Override
    public Like createLike(Like like) throws Exception {
        String sql = "INSERT INTO likes (user_id, post_id) VALUES (?, ?)";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, like.getUserId());
            ps.setInt(2, like.getPostId());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                like.setId(rs.getInt(1));
            }
        }
        return like;
    }

    @Override
    public Like getLikeById(int id) throws Exception {
        String sql = "SELECT * FROM likes WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractLike(rs);
            }
        }
        return null;
    }

    @Override
    public List<Like> getLikesByPostId(int postId) throws Exception {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes WHERE post_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                likes.add(extractLike(rs));
            }
        }
        return likes;
    }

    @Override
    public List<Like> getLikesByUserId(int userId) throws Exception {
        List<Like> likes = new ArrayList<>();
        String sql = "SELECT * FROM likes WHERE user_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                likes.add(extractLike(rs));
            }
        }
        return likes;
    }

    @Override
    public int getLikeCountByPostId(int postId) throws Exception {
        String sql = "SELECT COUNT(*) FROM likes WHERE post_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    @Override
    public boolean deleteLike(int id) throws Exception {
        String sql = "DELETE FROM likes WHERE id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteLikeByUserAndPost(int userId, int postId) throws Exception {
        String sql = "DELETE FROM likes WHERE user_id = ? AND post_id = ?";
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, postId);
            return ps.executeUpdate() > 0;
        }
    }

    private Like extractLike(ResultSet rs) throws SQLException {
        Like like = new Like();
        like.setId(rs.getInt("id"));
        like.setUserId(rs.getInt("user_id"));
        like.setPostId(rs.getInt("post_id"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            like.setCreatedAt(ts.toLocalDateTime());
        }
        return like;
    }
}
